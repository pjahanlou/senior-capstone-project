package com.example.seizuredetectionapp.profile.blecallbacks;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public interface BleAccYCallback {

    /**
     * Called when the data has been sent to the connected device.
     *
     * @param device the target device.
     * @param val true when Acc was enabled, false when disabled.
     */
    void onAccYValChanged(@NonNull final BluetoothDevice device, final String val);
}
