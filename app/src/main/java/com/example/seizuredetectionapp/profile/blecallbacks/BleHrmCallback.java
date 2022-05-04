package com.example.seizuredetectionapp.profile.blecallbacks;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public interface BleHrmCallback {

    /**
     * Called when the data has been sent to the connected device.
     *
     * @param device the target device.
     * @param val true when LED was enabled, false when disabled.
     */
    void onHrmValChanged(@NonNull final BluetoothDevice device, final int val);
}