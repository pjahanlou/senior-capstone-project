package com.example.seizuredetectionapp.profile.blecallbacks;

import android.bluetooth.BluetoothDevice;
import androidx.annotation.NonNull;

import no.nordicsemi.android.ble.callback.DataSentCallback;
import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback;
import no.nordicsemi.android.ble.data.Data;

@SuppressWarnings("ConstantConditions")
public abstract class BleHrmDataCallback implements ProfileDataCallback, DataSentCallback, BleHrmCallback {

    @Override
    public void onDataReceived(@NonNull final BluetoothDevice device, @NonNull final Data data) {
        parse(device, data);
    }

    @Override
    public void onDataSent(@NonNull final BluetoothDevice device, @NonNull final Data data) {
        parse(device, data);
    }

    private void parse(@NonNull final BluetoothDevice device, @NonNull final Data data) {
        if (data.size() != 1) {
            onInvalidDataReceived(device, data);
            return;
        }

        final int value = data.getIntValue(Data.FORMAT_UINT8, 0);
        onHrmValChanged(device, value);
    }
}