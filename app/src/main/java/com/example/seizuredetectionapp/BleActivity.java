package com.example.seizuredetectionapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;

import com.example.seizuredetectionapp.adapter.DiscoveredBluetoothDevice;
import com.example.seizuredetectionapp.databinding.ActivityBleBinding;
import com.example.seizuredetectionapp.viewmodels.STRappBleViewModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.gson.Gson;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;

import no.nordicsemi.android.ble.livedata.state.ConnectionState;
import no.nordicsemi.android.ble.observer.ConnectionObserver;

public class BleActivity extends AppCompatActivity {
    public static final String EXTRA_DEVICE = "com.example.seizuredetectionapp.EXTRA_DEVICE";

    private STRappBleViewModel viewModel;
    private ActivityBleBinding binding;
    private SharedPreferences sharedPreferences;
    private LocalSettings localSettings;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sharedPreferences = getSharedPreferences(localSettings.PREFERENCES, Context.MODE_PRIVATE);
        final Intent intent = getIntent();
        final DiscoveredBluetoothDevice device = intent.getParcelableExtra(EXTRA_DEVICE);
        saveDevice(device);
        final String deviceName = device.getName();
        final String deviceAddress = device.getAddress();
        onConnectionStateChanged(true);

//        final MaterialToolbar toolbar = binding.toolbar;
//        toolbar.setTitle(deviceName != null ? deviceName : getString(R.string.unknown_device));
//        toolbar.setSubtitle(deviceAddress);
//        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        final Intent dataSTRappIntent = new Intent(this, Navbar.class);
//        startActivity(dataSTRappIntent);

        // Configure the view model.
        viewModel = new ViewModelProvider(this).get(STRappBleViewModel.class);
//        saveViewModel(viewModel);
//        Log.d("device", String.valueOf(device));
//        Log.d("EXTRA_DEVICE", EXTRA_DEVICE);
        viewModel.connect(device);

        // Set up views.
        binding.ledSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> viewModel.setLedState(isChecked));
        binding.infoNotSupported.actionRetry.setOnClickListener(v -> viewModel.reconnect());
        binding.infoTimeout.actionRetry.setOnClickListener(v -> viewModel.reconnect());
        viewModel.getConnectionState().observe(this, state -> {
            switch (state.getState()) {
                case CONNECTING:
                    binding.progressContainer.setVisibility(View.VISIBLE);
                    binding.infoNotSupported.container.setVisibility(View.GONE);
                    binding.infoTimeout.container.setVisibility(View.GONE);
                    binding.connectionState.setText(R.string.state_connecting);
                    break;
                case INITIALIZING:
                    binding.connectionState.setText(R.string.state_initializing);
                    break;
                case READY:
                    binding.progressContainer.setVisibility(View.GONE);
//                    binding.deviceContainer.setVisibility(View.VISIBLE);
                    onConnectionStateChanged(true);
//                    final Intent dataSTRappIntent = new Intent(this, AppSettings.class);
//                    startActivity(dataSTRappIntent);
                    break;
                case DISCONNECTED:
                    if (state instanceof ConnectionState.Disconnected) {
                        binding.deviceContainer.setVisibility(View.GONE);
                        binding.progressContainer.setVisibility(View.GONE);
                        final ConnectionState.Disconnected stateWithReason = (ConnectionState.Disconnected) state;
                        if (stateWithReason.getReason() == ConnectionObserver.REASON_NOT_SUPPORTED) {
                            binding.infoNotSupported.container.setVisibility(View.VISIBLE);
                        } else {
                            binding.infoTimeout.container.setVisibility(View.VISIBLE);
                        }
                    }
                    // fallthrough
                case DISCONNECTING:
                    onConnectionStateChanged(false);
                    break;
            }
        });
        final Intent dataSTRappIntent = new Intent(this, Navbar.class);
        startActivity(dataSTRappIntent);
        viewModel.getLedState().observe(this, isOn -> {
            binding.ledState.setText(isOn ? R.string.turn_on : R.string.turn_off);
            binding.ledSwitch.setChecked(isOn);
        });
        LiveData<String> AccX = viewModel.getAccxData();
        Log.d("AccX", String.valueOf(AccX));
        Log.d("AccX", String.valueOf(AccX));
        viewModel.getAccxData().observe(this,
                pressed -> binding.buttonState.setText(String.format(Objects.requireNonNull(viewModel.getAccxData().getValue()))));
    }

    public void saveDevice(DiscoveredBluetoothDevice device){
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String deviceJson = gson.toJson(device);
        prefsEditor.putString("device", deviceJson);
        if(prefsEditor.commit()){
            Log.d("device", "device save successful");
        } else{
            Log.d("device", "device save not successful");
        }
    }

    private void onConnectionStateChanged(final boolean connected) {
        binding.ledSwitch.setEnabled(connected);
        if (!connected) {
            binding.ledSwitch.setChecked(false);
            binding.buttonState.setText(R.string.button_unknown);
        }
    }
}
