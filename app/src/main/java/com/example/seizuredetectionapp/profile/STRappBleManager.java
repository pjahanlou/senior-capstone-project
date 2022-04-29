package com.example.seizuredetectionapp.profile;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.seizuredetectionapp.BuildConfig;
import com.example.seizuredetectionapp.profile.blecallbacks.BleLedDataCallback;
import com.example.seizuredetectionapp.profile.blecallbacks.BleSensorDataCallback;
import com.example.seizuredetectionapp.profile.bledata.BleLED;

import java.util.UUID;

import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.ble.livedata.ObservableBleManager;
import no.nordicsemi.android.log.LogContract;
import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;

public class STRappBleManager extends ObservableBleManager {
    /** STRapp Service UUID. */
    public final static UUID STR_UUID_SERVICE = UUID.fromString("96254b66-7ed3-449c-9030-7f6313711921");
    /** EDA characteristic UUID. */
    private final static UUID STR_UUID_CHR_ELECTRODERMAL = UUID.fromString("96254b67-7ed3-449c-9030-7f6313711921");
    /** Heart rate characteristic UUID. */
    private final static UUID STR_UUID_CHR_HEARTRATE = UUID.fromString("96254b68-7ed3-449c-9030-7f6313711921");
    /** Accelerometer characteristic UUID. */
    private final static UUID STR_UUID_CHR_ACCELEROMETER = UUID.fromString("96254b69-7ed3-449c-9030-7f6313711921");
    /** Gyroscope characteristic UUID. */
    private final static UUID STR_UUID_CHR_GYROSCOPE = UUID.fromString("96254b6a-7ed3-449c-9030-7f6313711921");
    /** Temperature characteristic UUID. */
    private final static UUID STR_UUID_CHR_TEMPERATURE = UUID.fromString("96254b6b-7ed3-449c-9030-7f6313711921");

    private final MutableLiveData<Boolean> ledState = new MutableLiveData<>();
    private final MutableLiveData<String> accData = new MutableLiveData<>();
    private final MutableLiveData<String> gyroData = new MutableLiveData<>();
    private final MutableLiveData<String> edaData = new MutableLiveData<>();
    private final MutableLiveData<String> hrmData = new MutableLiveData<>();
    private final MutableLiveData<String> tmpData = new MutableLiveData<>();
    private final MutableLiveData<String> timeData = new MutableLiveData<>();
    private final MutableLiveData<String> battData = new MutableLiveData<>();
    private final MutableLiveData<String> sensorData = new MutableLiveData<>();

    private BluetoothGattCharacteristic ledCharacteristic, accCharacteristic, gyroCharacteristic, edaCharacteristic, tempCharacteristic, sensorCharacteristic;
    private LogSession logSession;
    private boolean supported;
    private boolean ledOn;

    public STRappBleManager(@NonNull Context context) {
        super(context);
    }

    public final LiveData<Boolean> getLedState() {
        return ledState;
    }

    public final LiveData<String> getAccData() {
        return accData;
    }

    public final LiveData<String> getSensorData() {
        return sensorData;
    }

    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return new STRappBleManagerGattCallback();
    }

    /**
     * Sets the log session to be used for low level logging.
     * @param session the session, or null, if nRF Logger is not installed.
     */
    public void setLogger(@Nullable final LogSession session) {
        logSession = session;
    }

    @Override
    public void log(final int priority, @NonNull final String message) {
        if (BuildConfig.DEBUG) {
            Log.println(priority, "STRappBleManager", message);
        }
        // The priority is a Log.X constant, while the Logger accepts it's log levels.
        Logger.log(logSession, LogContract.Log.Level.fromPriority(priority), message);
    }

    @Override
    protected boolean shouldClearCacheWhenDisconnected() {
        return !supported;
    }

    /**
     * The Sensor callback will be notified when a notification from Sensor characteristic
     * has been received, or its data was read.
     * <p>
     * If the data received are valid (a string), the
     * {@link BleSensorDataCallback#onSensorStateChanged} will be called.
     * Otherwise, the {@link BleSensorDataCallback#onInvalidDataReceived(BluetoothDevice, Data)}
     * will be called with the data received.
     */
    private	final BleSensorDataCallback sensorCallback = new BleSensorDataCallback() {
        @SuppressLint("WrongConstant")
        @Override
        public void onSensorStateChanged(@NonNull final BluetoothDevice device,
                                         final boolean data) {
            log(LogContract.Log.Level.APPLICATION, "Data: " + (data));
            // The STRappManager is initialized with a default Handler, which will use
            // UI thread for the callbacks. setValue can be called safely.
            // If you're using a different handler, or coroutines, use postValue(..) instead.
            sensorData.setValue(String.valueOf(data));
        }

        @Override
        public void onInvalidDataReceived(@NonNull final BluetoothDevice device,
                                          @NonNull final Data data) {
            log(Log.WARN, "Invalid data received: " + data);
        }
    };

    /**
     * The LED callback will be notified when the LED state was read or sent to the target device.
     * <p>
     * This callback implements both {@link no.nordicsemi.android.ble.callback.DataReceivedCallback}
     * and {@link no.nordicsemi.android.ble.callback.DataSentCallback} and calls the same
     * method on success.
     * <p>
     * If the data received were invalid, the
     * {@link BleLedDataCallback#onInvalidDataReceived(BluetoothDevice, Data)} will be
     * called.
     */
    private final BleLedDataCallback ledCallback = new BleLedDataCallback() {
        @SuppressLint("WrongConstant")
        @Override
        public void onLedStateChanged(@NonNull final BluetoothDevice device,
                                      final boolean on) {
            ledOn = on;
            log(LogContract.Log.Level.APPLICATION, "LED " + (on ? "ON" : "OFF"));
            // The STRappManager is initialized with a default Handler, which will use
            // UI thread for the callbacks. setValue can be called safely.
            // If you're using a different handler, or coroutines, use postValue(..) instead.
            ledState.setValue(on);
        }

        @Override
        public void onInvalidDataReceived(@NonNull final BluetoothDevice device,
                                          @NonNull final Data data) {
            // Data can only invalid if we read them. We assume the app always sends correct data.
            log(Log.WARN, "Invalid data received: " + data);
        }
    };


    /**
     * BluetoothGatt callbacks object.
     */
    private class STRappBleManagerGattCallback extends BleManagerGattCallback {
        @Override
        protected void initialize() {
//            setNotificationCallback(timestampCharacteristic).with(timestampCallback);
            setNotificationCallback(sensorCharacteristic).with(sensorCallback);
            readCharacteristic(sensorCharacteristic).with(sensorCallback).enqueue();
            enableNotifications(sensorCharacteristic).enqueue();
//            enableNotifications(timestampCharacteristic).enqueue();
        }

        @Override
        public boolean isRequiredServiceSupported(@NonNull final BluetoothGatt gatt) {
            final BluetoothGattService service = gatt.getService(STR_UUID_SERVICE);
            if (service != null) {
                sensorCharacteristic = service.getCharacteristic(STR_UUID_CHR_ELECTRODERMAL);
                ledCharacteristic = service.getCharacteristic(STR_UUID_CHR_HEARTRATE);
            }

            boolean writeRequest = false;
            if (ledCharacteristic != null) {
                final int ledProperties = ledCharacteristic.getProperties();
                writeRequest = (ledProperties & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;
            }

            supported = sensorCharacteristic != null && ledCharacteristic != null && writeRequest;
            return supported;
        }

        @Override
        protected void onServicesInvalidated() {
            sensorCharacteristic = null;
            ledCharacteristic = null;
        }
    }

    /**
     * Sends a request to the device to turn the LED on or off.
     *
     * @param on true to turn the LED on, false to turn it off.
     */
    public void turnLed(final boolean on) {
        // Are we connected?
        if (ledCharacteristic == null)
            return;

        // No need to change?
        if (ledOn == on)
            return;

        log(Log.VERBOSE, "Turning LED " + (on ? "ON" : "OFF") + "...");
        writeCharacteristic(
                ledCharacteristic,
                BleLED.turn(on),
                BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
        ).with(ledCallback).enqueue();
    }
}
