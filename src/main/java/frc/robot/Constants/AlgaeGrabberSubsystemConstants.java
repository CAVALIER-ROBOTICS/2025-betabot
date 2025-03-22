// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.Constants;

import edu.wpi.first.math.kinematics.ChassisSpeeds;

/** Add your docs here. */
public class AlgaeGrabberSubsystemConstants {
    public static final int PIVOT_MOTOR_ID = 41;
    public static final int SPIN_MOTOR_ID = 40;

    public static final int THRU_BORE_ENCODER_ID = 0;

    public static final double ALGAE_REMOVAL_ENCODER_POSITION = -.25;
    public static final double PROCESSOR_SCORING_ENCODER_POSITION = -.3;
    public static final double RETRACTED_ENCODER_POSITION = 0.3;
    public static final double EJECT_ENCODER_POSITION = 0.3;
    public static final double GROUND_INTAKE_ENCODER_POSITION = -.3;

    public static final double MINIMUM_SAFE_ELEVATOR_ENCODER_POSITION = 12.0; //Sometimes mechanical's methods are a delightful gift to programming.

    public static final double INTAKE_MOTOR_SPEED = 1.0;

    public static final double INTAKE_CURRENT_DRAW = 40.0;

    public static final ChassisSpeeds INTAKE_CHASSIS_SPEEDS = new ChassisSpeeds(-.5, 0, 0);
    public static final ChassisSpeeds RETRACT_CHASSIS_SPEEDS = new ChassisSpeeds(1, 0, 0);
}
