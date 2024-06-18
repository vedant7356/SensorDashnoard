package com.example.sensordashboard;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope, magnetometer, proximity, light, barometer, temperature;
    private TextView accelTextView, gyroData, magData, proxData, lightData, baroData, tempData;
    private LineChart lineChartAccel, lineChartGyro, lineChartMag, lineChartLight, lineChartBaro, lineChartTemp;
    private BarChart barChartProx;
    private ArrayList<Entry> accelEntries = new ArrayList<>();
    private ArrayList<Entry> gyroEntries = new ArrayList<>();
    private ArrayList<Entry> magEntries = new ArrayList<>();
    private ArrayList<Entry> lightEntries = new ArrayList<>();
    private ArrayList<Entry> baroEntries = new ArrayList<>();
    private ArrayList<Entry> tempEntries = new ArrayList<>();
    private ArrayList<BarEntry> proxEntries = new ArrayList<>();
    private LineDataSet accelDataSet, gyroDataSet, magDataSet, lightDataSet, baroDataSet, tempDataSet;
    private LineData lineChartAccelData, lineChartGyroData, lineChartMagData, lineChartLightData, lineChartBaroData, lineChartTempData;
    private BarDataSet proxDataSet;
    private BarData barChartProxData;
    private float lightThreshold = 1000; // User-defined threshold for light sensor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize TextViews
        accelTextView = findViewById(R.id.accelData);
        gyroData = findViewById(R.id.gyroData);
        magData = findViewById(R.id.magData);
        proxData = findViewById(R.id.proxData);
        lightData = findViewById(R.id.lightData);
        baroData = findViewById(R.id.baroData);
        tempData = findViewById(R.id.tempData);

        // Initialize LineCharts
        lineChartAccel = findViewById(R.id.lineChartAccel);
        lineChartGyro = findViewById(R.id.lineChartGyro);
        lineChartMag = findViewById(R.id.lineChartMag);
        lineChartLight = findViewById(R.id.lineChartLight);
        lineChartBaro = findViewById(R.id.lineChartBaro);
        lineChartTemp = findViewById(R.id.lineChartTemp);

        // Initialize BarChart
        barChartProx = findViewById(R.id.barChartProx);

        // Initialize DataSets
        accelDataSet = new LineDataSet(accelEntries, "Accelerometer Data");
        gyroDataSet = new LineDataSet(gyroEntries, "Gyroscope Data");
        magDataSet = new LineDataSet(magEntries, "Magnetometer Data");
        lightDataSet = new LineDataSet(lightEntries, "Light Sensor Data");
        baroDataSet = new LineDataSet(baroEntries, "Barometer Data");
        tempDataSet = new LineDataSet(tempEntries, "Temperature Data");
        proxDataSet = new BarDataSet(proxEntries, "Proximity Data");

        // Initialize LineData
        lineChartAccelData = new LineData(accelDataSet);
        lineChartGyroData = new LineData(gyroDataSet);
        lineChartMagData = new LineData(magDataSet);
        lineChartLightData = new LineData(lightDataSet);
        lineChartBaroData = new LineData(baroDataSet);
        lineChartTempData = new LineData(tempDataSet);

        // Initialize BarData
        barChartProxData = new BarData(proxDataSet);

        // Set Data to Charts
        lineChartAccel.setData(lineChartAccelData);
        lineChartGyro.setData(lineChartGyroData);
        lineChartMag.setData(lineChartMagData);
        lineChartLight.setData(lineChartLightData);
        lineChartBaro.setData(lineChartBaroData);
        lineChartTemp.setData(lineChartTempData);
        barChartProx.setData(barChartProxData);

        // Initialize SensorManager and Sensors
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        barometer = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        temperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);

        // Register listeners for sensors
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);

        // Check and register barometer sensor
        if (barometer != null) {
            sensorManager.registerListener(this, barometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            baroData.setText("Barometer sensor not available on this device.");
        }

        // Check and register temperature sensor
        if (temperature != null) {
            sensorManager.registerListener(this, temperature, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            tempData.setText("Temperature sensor not available on this device.");
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                updateLineChart(event.values, accelTextView, accelEntries, accelDataSet, lineChartAccelData, lineChartAccel);
                break;
            case Sensor.TYPE_GYROSCOPE:
                updateLineChart(event.values, gyroData, gyroEntries, gyroDataSet, lineChartGyroData, lineChartGyro);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                updateLineChart(event.values, magData, magEntries, magDataSet, lineChartMagData, lineChartMag);
                break;
            case Sensor.TYPE_PROXIMITY:
                updateBarChart(event.values[0], proxData, proxEntries, proxDataSet, barChartProxData, barChartProx);
                break;
            case Sensor.TYPE_LIGHT:
                updateLineChart(event.values, lightData, lightEntries, lightDataSet, lineChartLightData, lineChartLight);
                if (event.values[0] < lightThreshold) {
                  //  triggerAlert("Light level is below threshold!");
                }
                break;
            case Sensor.TYPE_PRESSURE:
                updateLineChart(event.values, baroData, baroEntries, baroDataSet, lineChartBaroData, lineChartBaro);
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                updateLineChart(event.values, tempData, tempEntries, tempDataSet, lineChartTempData, lineChartTemp);
                break;
        }
    }

    private void updateLineChart(float[] values, TextView textView, ArrayList<Entry> entries, LineDataSet dataSet, LineData lineData, LineChart lineChart) {
        StringBuilder sensorData = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            sensorData.append("Value ").append(i).append(": ").append(values[i]).append("\n");
            entries.add(new Entry(entries.size(), values[i])); // Use the value for now
        }
        textView.setText(sensorData.toString());
        dataSet.notifyDataSetChanged();
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        lineChart.invalidate();
    }

    private void updateBarChart(float value, TextView textView, ArrayList<BarEntry> entries, BarDataSet dataSet, BarData barData, BarChart barChart) {
        textView.setText("Proximity: " + value);
        entries.add(new BarEntry(entries.size(), value));
        dataSet.notifyDataSetChanged();
        barData.notifyDataChanged();
        barChart.notifyDataSetChanged();
        barChart.invalidate();
    }

    private void triggerAlert(String message) {
        // Implement alert mechanism (e.g., Toast, Notification, Sound)
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle sensor accuracy changes if needed
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, barometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, temperature, SensorManager.SENSOR_DELAY_NORMAL);
    }
}
