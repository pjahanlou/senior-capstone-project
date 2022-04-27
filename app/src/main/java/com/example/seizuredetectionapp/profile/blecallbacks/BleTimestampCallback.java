package com.example.seizuredetectionapp.profile.blecallbacks;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public interface BleTimestampCallback {

    /**
     * Called when the data has been sent to the connected device.
     *
     * @param device the target device.
     * @param changed true when Timestamp is changed, false when Timestamp is unchanged.
     */
    void onTimestampStateChanged(@NonNull final BluetoothDevice device, final boolean data);
}
