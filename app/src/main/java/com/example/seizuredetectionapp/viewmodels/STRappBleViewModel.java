package com.example.seizuredetectionapp.viewmodels;

import android.app.Application;
import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import no.nordicsemi.android.ble.ConnectRequest;
import no.nordicsemi.android.ble.livedata.state.ConnectionState;
import com.example.seizuredetectionapp.adapter.DiscoveredBluetoothDevice;
import com.example.seizuredetectionapp.profile.STRappBleManager;
import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.log.Logger;

public class STRappBleViewModel extends AndroidViewModel {
    private final STRappBleManager strappBleManager;
    private BluetoothDevice device;
    @Nullable
    private ConnectRequest connectRequest;

    public STRappBleViewModel(@NonNull final Application application) {
        super(application);

        // Initialize the manager.
        strappBleManager = new STRappBleManager(getApplication());
    }

    public LiveData<ConnectionState> getConnectionState() {
        return strappBleManager.state;
    }

    public LiveData<String> getSensorState() {
        return strappBleManager.getSensorData();
    }

    public LiveData<Boolean> getLedState() {
        return strappBleManager.getLedState();
    }

    public LiveData<String> getAccxData() { return strappBleManager.getAccxData(); }

    public LiveData<String> getAccyData() { return strappBleManager.getAccyData(); }

    public LiveData<String> getAcczData() { return strappBleManager.getAcczData(); }

    public LiveData<String> getGyroxData() { return strappBleManager.getGyroxData(); }

    public LiveData<String> getGyroyData() { return strappBleManager.getGyroyData(); }

    public LiveData<String> getGyrozData() { return strappBleManager.getGyrozData(); }

    public LiveData<Integer> getHrmData() { return strappBleManager.getHrmData(); }

    /**
     * Connect to the given peripheral.
     *
     * @param target the target device.
     */
    public void connect(@NonNull final DiscoveredBluetoothDevice target) {
        // Prevent from calling again when called again (screen orientation changed).
        if (device == null) {
            device = target.getDevice();
            final LogSession logSession = Logger
                    .newSession(getApplication(), null, target.getAddress(), target.getName());
            strappBleManager.setLogger(logSession);
            reconnect();
        }
    }

    /**
     * Reconnects to previously connected device.
     * If this device was not supported, its services were cleared on disconnection, so
     * reconnection may help.
     */
    public void reconnect() {
        if (device != null) {
            connectRequest = strappBleManager.connect(device)
                    .retry(3, 100)
                    .useAutoConnect(false)
                    .then(d -> connectRequest = null);
            connectRequest.enqueue();
        }
    }

    /**
     * Disconnect from peripheral.
     */
    private void disconnect() {
        device = null;
        if (connectRequest != null) {
            connectRequest.cancelPendingConnection();
        } else if (strappBleManager.isConnected()) {
            strappBleManager.disconnect().enqueue();
        }
    }

    /**
     * Sends a command to turn ON or OFF the LED on the nRF5 DK.
     *
     * @param on true to turn the LED on, false to turn it OFF.
     */
    public void setLedState(final boolean on) {
        strappBleManager.turnLed(on);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disconnect();
    }
}
