// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.utils;

import au.grapplerobotics.LaserCan;
import au.grapplerobotics.interfaces.LaserCanInterface.Measurement;

/** Add your docs here. */
public class CavbotsLaserCAN {
    LaserCan laserCan;
    public CavbotsLaserCAN(int id) {
        laserCan = new LaserCan(id);
    }

    public int getProximity() {
        Measurement measurement = laserCan.getMeasurement();
        if(measurement != null && measurement.status == LaserCan.LASERCAN_STATUS_VALID_MEASUREMENT) {
            return measurement.distance_mm;
        }
        return 1000;
    }
}
