package com.example.seizuredetectionapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

// TODO Look into replacing LocalBroadcastManager with something non-deprecated such as androidx.lifecycle.LiveData
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//@SuppressWarnings("deprecation")
@SuppressLint("MissingPermission")
public class BluetoothSerial {

    private Handler handler; // handler that gets info from Bluetooth service

    private static String STRAPP_BLUETOOTH = "STRappBluetooth";

    public static String BLUETOOTH_CONNECTED = "bluetooth-connection-started";

    public static String BLUETOOTH_DISCONNECTED = "bluetooth-connection-lost";

    public static String BLUETOOTH_FAILED = "bluetooth-connection-failed";

    boolean connected = false;

    BluetoothDevice bluetoothDevice;

    BluetoothSocket serialSocket;

    InputStream serialInputStream;

    OutputStream serialOutputStream;

    SerialReader serialReader;

    MessageHandler messageHandler;

    Context context;

    AsyncTask<Void, Void, BluetoothDevice> connectionTask;

    String devicePrefix;

    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
    }

    // Listens for discount message from bluetooth system and reestablishing a connection
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice eventDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                if (bluetoothDevice != null && bluetoothDevice.equals(eventDevice)){
                    Log.i(STRAPP_BLUETOOTH, "Received bluetooth disconnect notice");

                    //clean up any streams
                    close();

                    //reestablish connect
                    connect();

                    LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(BLUETOOTH_DISCONNECTED));
                }
            }
        }
    };

    public BluetoothSerial(Context context, MessageHandler messageHandler, String devicePrefix){
        this.context = context;
        this.messageHandler = messageHandler;
        this.devicePrefix = devicePrefix.toUpperCase();
    }

    public void onPause() {
        context.unregisterReceiver(bluetoothReceiver);
    }

    public void onResume() {
        //listen for bluetooth disconnect
        IntentFilter disconnectIntent = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        context.registerReceiver(bluetoothReceiver, disconnectIntent);

        //reestablishes a connection is one doesn't exist
        if(!connected){
            connect();
        } else {
            Intent intent = new Intent(BLUETOOTH_CONNECTED);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }


    // Initializes the bluetooth serial connections, uses the LocalBroadcastManager when connection is established
    @SuppressLint("StaticFieldLeak")
    public void connect(){

        if (connected){
            Log.e(STRAPP_BLUETOOTH,"Connection request while already connected");
            return;
        }

        if (connectionTask != null && connectionTask.getStatus()==AsyncTask.Status.RUNNING){
            Log.e(STRAPP_BLUETOOTH,"Connection request while attempting connection");
            return;
        }

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter== null || !bluetoothAdapter.isEnabled()) {
            return;
        }

        final List<BluetoothDevice> pairedDevices = new ArrayList<>(bluetoothAdapter.getBondedDevices());
        if (pairedDevices.size() > 0) {
            bluetoothAdapter.cancelDiscovery();

            // AsyncTask to handle the establishing of a bluetooth connection
            // TODO refactor to use non-deprecated java.util.concurrent to fix risk of leaks
            connectionTask = new AsyncTask<Void, Void, BluetoothDevice>(){

                final int MAX_ATTEMPTS = 30;

                int attemptCounter = 0;

                @Override
                protected BluetoothDevice doInBackground(Void... params) {
                    while(!isCancelled()){ //need to kill without calling onCancel

                        for (BluetoothDevice device : pairedDevices) {
                            if (device.getName().toUpperCase().startsWith(devicePrefix)){
                                Log.i(STRAPP_BLUETOOTH, attemptCounter + ": Attempting connection to " + device.getName());

                                try {

                                    try {
                                        // Standard SerialPortService ID
                                        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
                                        serialSocket = device.createRfcommSocketToServiceRecord(uuid);
                                    } catch (Exception ce){
                                        serialSocket = connectViaReflection(device);
                                    }

                                    //setup the connect streams
                                    serialSocket.connect();
                                    serialInputStream = serialSocket.getInputStream();
                                    serialOutputStream = serialSocket.getOutputStream();

                                    connected = true;
                                    Log.i(STRAPP_BLUETOOTH,"Connected to " + device.getName());

                                    return device;
                                } catch (Exception e) {
                                    serialSocket = null;
                                    serialInputStream=null;
                                    serialOutputStream=null;
                                    Log.i(STRAPP_BLUETOOTH, e.getMessage());
                                }
                            }
                        }

                        try {
                            attemptCounter++;
                            if (attemptCounter>MAX_ATTEMPTS)
                                this.cancel(false);
                            else
                                Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }

                    Log.i(STRAPP_BLUETOOTH, "Stopping connection attempts");

                    Intent intent = new Intent(BLUETOOTH_FAILED);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                    return null;
                }

                @Override
                protected void onPostExecute(BluetoothDevice result) {
                    super.onPostExecute(result);

                    bluetoothDevice = result;

                    //start thread responsible for reading from inputstream
                    serialReader = new SerialReader();
                    serialReader.start();

                    //send connection message
                    Intent intent = new Intent(BLUETOOTH_CONNECTED);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }

            };
            connectionTask.execute();
        }
    }

    private BluetoothSocket connectViaReflection(BluetoothDevice device) throws Exception {
        Method m = device.getClass().getMethod("createRfcommSocket", new Class[] {int.class});
        return (BluetoothSocket) m.invoke(device, 1);
    }

    // Returns an estimate of the number of bytes that can be read (or skipped over) from this input
    // stream without blocking, which may be 0, or 0 when end of stream is detected.
    public int available() throws IOException{
        if (connected)
            return serialInputStream.available();

        throw new RuntimeException("Connection lost, reconnecting now.");
    }

    // Reads the next byte of data from the input stream.
    public int read() throws IOException{
        if (connected)
            return serialInputStream.read();

        throw new RuntimeException("Connection lost, reconnecting now.");
    }

    // Reads some number of bytes from the input stream and stores them into the buffer array buffer.
    public int read(byte[] buffer) throws IOException{
        if (connected)
            return serialInputStream.read(buffer);

        throw new RuntimeException("Connection lost, reconnecting now.");
    }

    // Reads up to byteCount bytes of data from the input stream into an array of bytes.
    public int read(byte[] buffer, int byteOffset, int byteCount) throws IOException{
        if (connected)
            return serialInputStream.read(buffer, byteOffset, byteCount);

        throw new RuntimeException("Connection lost, reconnecting now.");
    }

    // Writes buffer.length bytes from the specified byte array to this output stream.
    public void write(byte[] buffer) throws IOException{
        if (connected)
            serialOutputStream.write(buffer);

        throw new RuntimeException("Connection lost, reconnecting now.");
    }

    // Writes the specified byte to this output stream.
    public void write(int oneByte) throws IOException{
        if (connected)
            serialOutputStream.write(oneByte);

        throw new RuntimeException("Connection lost, reconnecting now.");
    }

    // Writes len bytes from the specified byte array starting at offset off to this output stream.
    public void write(byte[] buffer, int offset, int count) throws IOException {
        serialOutputStream.write(buffer, offset, count);

        throw new RuntimeException("Connection lost, reconnecting now.");
    }

    private class SerialReader extends Thread {
        private static final int MAX_BYTES = 125;

        byte[] buffer = new byte[MAX_BYTES];

        int bufferSize = 0;

        public void run() {
            Log.i("serialReader", "Starting serial loop");
            while (!isInterrupted()) {
                try {

                    // check to see if there are unread bytes in the buffer
                    // if so add them to the buffer
                    if (available() > 0){

                        int newBytes = read(buffer, bufferSize, MAX_BYTES - bufferSize);
                        if (newBytes > 0)
                            bufferSize += newBytes;

                        Log.d(STRAPP_BLUETOOTH, "read " + newBytes);
                    }

                    if (bufferSize > 0) {
                        int read = messageHandler.read(bufferSize, buffer);

                        // shift unread data to start of buffer
                        if (read > 0) {
                            int index = 0;
                            for (int i = read; i < bufferSize; i++) {
                                buffer[index++] = buffer[i];
                            }
                            bufferSize = index;
                        }
                    } else {

                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException ie) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    Log.e(STRAPP_BLUETOOTH, "Error reading serial data", e);
                }
            }
            Log.i(STRAPP_BLUETOOTH, "Shutting serial loop");
        }
    }
     // Reads from the serial buffer, processing any available messages.  Must return the number of bytes consumed from the buffer
    public interface MessageHandler {
        int read(int bufferSize, byte[] buffer);
    }
    public class readMsg implements MessageHandler {
        public int read(int bufferSize, byte[] buffer) {
//            int[] msgArr = new int[bufferSize];
            StringBuilder msg = new StringBuilder();
            int leftBracketCount = 0;
            int rightBracketCount = 0;
            int bytesConsumed = 0;
            do{
                int byteRead;
                try {
                    byteRead = BluetoothSerial.this.read();
                    if(bytesConsumed == 0 && byteRead != 123){
                        bytesConsumed++;
                        return bytesConsumed;
                    }
                    bytesConsumed++;
                    if(byteRead == 123){
                        leftBracketCount++;
                    }
                    if(byteRead == 125){
                        rightBracketCount++;
                    }
                    if(byteRead == -1){
                        return 0;
                    }
//                    if(byteRead < 32 || byteRead > 126){
//                        return ;
//                    }
                    msg.append((char)byteRead);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }while(leftBracketCount != rightBracketCount);
            System.out.println(msg);
            System.out.println(bytesConsumed);
            return bytesConsumed;
        }
    }
    public class CallbackTest {
        public void onRead(MessageHandler doRead, int bufferSize, byte[] buffer) {
            doRead.read(bufferSize, buffer);
        }
    }
    public void close() {

        connected = false;

        if (connectionTask != null) {
            connectionTask.cancel(false);
        }
        if (serialReader != null) {
            serialReader.interrupt();

            try {
                serialReader.join(1000);
            } catch (InterruptedException ie) {}
        }
        try {
            serialInputStream.close();
        } catch (Exception e) {
            Log.e(STRAPP_BLUETOOTH, "Failed releasing inputstream connection");
        }
        try {
            serialOutputStream.close();
        } catch (Exception e) {
            Log.e(STRAPP_BLUETOOTH, "Failed releasing outputstream connection");
        }
        try {
            serialSocket.close();
        } catch (Exception e) {
            Log.e(STRAPP_BLUETOOTH, "Failed closing socket");
        }
        Log.i(STRAPP_BLUETOOTH, "Released bluetooth connections");
    }
}
