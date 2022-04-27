package com.example.seizuredetectionapp.profile.blecallbacks;

import android.bluetooth.BluetoothDevice;
import androidx.annotation.NonNull;

import no.nordicsemi.android.ble.callback.DataSentCallback;
import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback;
import no.nordicsemi.android.ble.data.Data;

@SuppressWarnings("ConstantConditions")
public abstract class BleSensorDataCallback implements ProfileDataCallback, DataSentCallback, BleSensorCallback {
    private static final byte STATE_OFF = 0x00;
    private static final byte STATE_ON = 0x01;

    @Override
    public void onDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data) {
        parse(device, data);
    }

    @Override
    public void onDataSent(@NonNull final BluetoothDevice device, @NonNull final Data data) {
        parse(device, data);
    }

    private void parse(@NonNull final BluetoothDevice device, @NonNull final Data data) {
        if (data.size() != 23) {
            onInvalidDataReceived(device, data);
            return;
        }

        final String state = data.getStringValue(Data.FORMAT_UINT8);
        if (state == STATE_ON) {
            onSensorStateChanged(device, true);
        } else if (state == STATE_OFF) {
            onSensorStateChanged(device, false);
        } else {
            onInvalidDataReceived(device, data);
        }
    }
}
