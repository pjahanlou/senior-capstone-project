package com.example.seizuredetectionapp.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

public class DeviceDiffCallback extends DiffUtil.ItemCallback<DiscoveredBluetoothDevice> {

    DeviceDiffCallback() {
    }

    @Override
    public boolean areItemsTheSame(@NonNull final DiscoveredBluetoothDevice oldItem, @NonNull final DiscoveredBluetoothDevice newItem) {
        return oldItem.equals(newItem);
    }

    @Override
    public boolean areContentsTheSame(@NonNull final DiscoveredBluetoothDevice oldItem, @NonNull final DiscoveredBluetoothDevice newItem) {
        return oldItem.hasRssiLevelChanged();
    }
}
