package com.example.sdp11.wmd;

/**
 * Created by nsanor on 2/10/2015.
 */
public class IMUDataPoint {
    private double accel_x;
    private double accel_y;
    private double accel_z;
    private double gyro_x;
    private double gyro_y;
    private double gyro_z;
    private double magnetometer_x;
    private double magnetometer_y;
    private double magnetometer_z;

    public IMUDataPoint(double accel_x, double accel_y, double accel_z, double gyro_x, double gyro_y, double gyro_z, double magnetometer_x, double magnetometer_y, double magnetometer_z)  {
        this.accel_x = accel_x;
        this.accel_y = accel_y;
        this.accel_z = accel_z;
        this.gyro_x = gyro_x;
        this.gyro_y = gyro_y;
        this.gyro_z = gyro_z;
        this.magnetometer_x = magnetometer_x;
        this.magnetometer_y = magnetometer_y;
        this.magnetometer_z = magnetometer_z;
    }
}
