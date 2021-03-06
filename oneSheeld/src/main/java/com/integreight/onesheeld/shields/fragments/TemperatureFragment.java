package com.integreight.onesheeld.shields.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.integreight.onesheeld.R;
import com.integreight.onesheeld.shields.ShieldFragmentParent;
import com.integreight.onesheeld.shields.controller.TemperatureShield;
import com.integreight.onesheeld.shields.controller.TemperatureShield.TemperatureEventHandler;
import com.integreight.onesheeld.utils.Log;

public class TemperatureFragment extends
        ShieldFragmentParent<TemperatureFragment> {
    TextView temperature_float, temperature_byte;
    TextView devicehasSensor;
    Button stoplistening_bt, startlistening_bt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.temperature_shield_fragment_layout,
                container, false);
        setHasOptionsMenu(true);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getApplication().getRunningShields().get(getControllerTag()) == null) {
            if (!reInitController())
                return;
        }
        ((TemperatureShield) getApplication().getRunningShields().get(
                getControllerTag())).registerSensorListener(true);

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        Log.d("Temperature Sheeld::OnActivityCreated()", "");

        temperature_float = (TextView) v
                .findViewById(R.id.temperature_float_txt);
        temperature_byte = (TextView) v.findViewById(R.id.temperature_byte_txt);

        devicehasSensor = (TextView) v
                .findViewById(R.id.device_not_has_sensor_text);
        stoplistening_bt = (Button) v.findViewById(R.id.stop_listener_bt);
        startlistening_bt = (Button) v.findViewById(R.id.start_listener_bt);

        startlistening_bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ((TemperatureShield) getApplication().getRunningShields().get(
                        getControllerTag())).registerSensorListener(true);
                temperature_float.setVisibility(View.VISIBLE);
                temperature_byte.setVisibility(View.VISIBLE);

            }
        });

        stoplistening_bt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                ((TemperatureShield) getApplication().getRunningShields().get(
                        getControllerTag())).unegisterSensorListener();
                temperature_float.setVisibility(View.INVISIBLE);
                temperature_byte.setVisibility(View.INVISIBLE);

            }
        });

    }

    private TemperatureEventHandler temperatureEventHandler = new TemperatureEventHandler() {

        @Override
        public void onSensorValueChangedFloat(final String value) {
            // TODO Auto-generated method stub
            if (canChangeUI()) {

                // set data to UI
                temperature_float.post(new Runnable() {

                    @Override
                    public void run() {
                        temperature_float.setVisibility(View.VISIBLE);
                        temperature_float.setText("" + value);
                    }
                });

            }

        }

        @Override
        public void onSensorValueChangedByte(final String value) {
            // TODO Auto-generated method stub

            // set data to UI
            temperature_byte.post(new Runnable() {

                @Override
                public void run() {
                    if (canChangeUI()) {
                        temperature_byte.setVisibility(View.VISIBLE);
                        temperature_byte.setText("temperature in Byte = "
                                + value);
                    }
                }
            });

        }

        @Override
        public void isDeviceHasSensor(final Boolean hasSensor) {
        }
    };

    private void initializeFirmata() {
        if (getApplication().getRunningShields().get(getControllerTag()) == null) {
            getApplication().getRunningShields().put(getControllerTag(),
                    new TemperatureShield(activity, getControllerTag()));

        }

    }

    public void doOnServiceConnected() {
        initializeFirmata();
    }

    ;

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (getApplication().getRunningShields().get(getControllerTag()) != null)
            ((TemperatureShield) getApplication().getRunningShields().get(
                    getControllerTag()))
                    .setTemperatureEventHandler(temperatureEventHandler);

    }
}
