package com.example.administrator.myapplication3.service;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.WorkerThread;

/**
 * Created by Administrator on 2017/2/20.
 */
 class AccelerometerListener implements SensorEventListener {

    private LocationManager locationManager;
    public double latitude = 0;
    public double longitude = 0;
    public double distance = 0;
    public double speed = 0;
    private Location lastKnownLocation;
    boolean locationChanged = false;
    private long currentTime = 0;
    private long lastTime = 0;
    private WorkerThread worker;
    protected static final int STOP = 1;
    protected static final int POSCHANGED = 2;
    protected static final int UPDATE = 3;
    //definition for the Accelerometer
    SensorManager sensorManager;
    boolean first_accelerometer = true;
    boolean flag = true;
    float[] values;
    AccelerometerListener accelerometerListener = new AccelerometerListener();
    long lastTime_acc = 0;
    long currentTime_acc = 0;
    double x1,y1,z1,x2,y2,z2,x3,y3,z3,length1,length2,length3;
    int steps = 0;
    int countSteps = 0;
    int countNewSteps = 0;
    //definition for the orintation
    boolean first_orietation = true;
    float currentDegree = 0;
    float lastDegree = 0;
    float degree = 0;
    //definition for the dead reckoning
    double stepsDistance = 0;
    int earthRadius = 6381000;
    double stepLength = 0.75;
    double longitude_dr = 0;
    double latitude_dr = 0;
    double curLongitude = 0;
    double curLatitude = 0;
    long lastTime_ori = 0;
    long currentTime_ori = 0;
    boolean first = true;
    int countSteps_dr = 0;
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        if (first_accelerometer)
        {
            x1 = event.values[SensorManager.DATA_X];
            y1 = event.values[SensorManager.DATA_Y];
            z1 = event.values[SensorManager.DATA_Z];
            x2 = event.values[SensorManager.DATA_X];
            y2 = event.values[SensorManager.DATA_Y];
            z2 = event.values[SensorManager.DATA_Z];
            x3 = event.values[SensorManager.DATA_X];
            y3 = event.values[SensorManager.DATA_Y];
            z3 = event.values[SensorManager.DATA_Z];
            first_accelerometer = false;
        }else{
            x3 = x2;
            y3 = y2;
            z3 = z2;
            x2 = x1;
            y2 = y1;
            z2 = z1;
            x1=event.values[SensorManager.DATA_X];
            y1=event.values[SensorManager.DATA_Y];
            z1=event.values[SensorManager.DATA_Z];
            length1=(Math.sqrt(x1*x1+y1*y1+z1*z1));
            length2=(Math.sqrt(x2*x2+y2*y2+z2*z2));
            length3=(Math.sqrt(x3*x3+y3*y3+z3*z3));

            if(length2 > 12.5){
                if(length2 > length3 && length2 > length1){
                    steps = 1;
                }
            }

            if(steps ==1 && length2 < 8.5){
                if(length2 < length3 && length2 < length1){
                    if(flag){
                        currentTime_acc = System.currentTimeMillis();
                        countSteps++;
                        steps = 0;
                        flag = false;
                    }else{
                        currentTime_acc = System.currentTimeMillis();
                        steps = 0;
                        if(currentTime_acc - lastTime_acc > 30 && currentTime_acc - lastTime_acc < 2000){
                            countSteps++;
                        }
                    }
                    lastTime_acc = currentTime_acc;
                }
            }

        }
    }
}