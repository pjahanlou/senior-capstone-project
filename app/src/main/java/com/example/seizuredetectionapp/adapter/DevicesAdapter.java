package com.example.seizuredetectionapp.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.seizuredetectionapp.R;
import com.example.seizuredetectionapp.ScannerActivity;
import com.example.seizuredetectionapp.viewmodels.DevicesLiveData;

import com.example.seizuredetectionapp.databinding.DeviceItemBinding;

public class DevicesAdapter extends ListAdapter<DiscoveredBluetoothDevice, DevicesAdapter.ViewHolder> {
    private static final DiffUtil.ItemCallback<DiscoveredBluetoothDevice> DIFFER = new DeviceDiffCallback();
    private OnItemClickListener onItemClickListener;

    @FunctionalInterface
    public interface OnItemClickListener {
        void onItemClick(@NonNull final DiscoveredBluetoothDevice device);
    }

    public void setOnItemClickListener(@Nullable final OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public DevicesAdapter(@NonNull final ScannerActivity activity,
                          @NonNull final DevicesLiveData devicesLiveData) {
        super(DIFFER);
        setHasStableIds(true);
        devicesLiveData.observe(activity, this::submitList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_item, parent, false);
        return new ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.bind(getItem(position));
    }

    @Override
    public long getItemId(final int position) {
        return getItem(position).hashCode();
    }

    final class ViewHolder extends RecyclerView.ViewHolder {
        private final DeviceItemBinding binding;

        private ViewHolder(@NonNull final View view) {
            super(view);
            binding = DeviceItemBinding.bind(view);
            binding.deviceContainer.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    final DiscoveredBluetoothDevice device = getItem(getBindingAdapterPosition());
                    onItemClickListener.onItemClick(device);
                }
            });
        }

        private void bind(@NonNull final DiscoveredBluetoothDevice device) {
            final String deviceName = device.getName();

            if (!TextUtils.isEmpty(deviceName))
                binding.deviceName.setText(deviceName);
            else
                binding.deviceName.setText(R.string.unknown_device);
            binding.deviceAddress.setText(device.getAddress());
            final int rssiPercent = (int) (100.0f * (127.0f + device.getRssi()) / (127.0f + 20.0f));
            binding.rssi.setImageLevel(rssiPercent);
        }
    }
}
