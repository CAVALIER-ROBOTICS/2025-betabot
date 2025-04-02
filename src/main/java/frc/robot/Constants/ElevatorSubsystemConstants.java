// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.Constants;

/** Add your docs here. */
public class ElevatorSubsystemConstants 
{
    public static final int LEFT_MOTOR_ID = 50;
    public static final int RIGHT_MOTOR_ID = 51;
    public static final int GRABBER_MOTOR_ID = 58;

    public static final int CORAL_SENSOR_ID = 37;
    public static final int CORAL_SENSOR_PROXIMITY_THRESHOLD = 20;

    public static final int NEO550_CURRENT_LIMIT = 30;

    public static final double L1_ENCODER_POSITION = 0.0;
    public static final double L2_ENCODER_POSITION = 8.5;
    public static final double L3_ENCODER_POSITION = 25.0;
    public static final double L4_ENCODER_POSITION = 52.0;

    public static final double HP_ENCODER_POSITION = 0.1;
    public static final double DEFAULT_POSITION = 1.5; //So the carriage doesn't slam into the base.

    public static final double HIGH_ALGAE_POSITION = 44.0;
    public static final double LOW_ALGAE_POSITION = 28.0;
    public static final double PROCESSOR_SCORE_POSITION = 4.0;
    public static final double GROUND_INTAKE_POSITION = 1.5;

    public static final double GRABBER_SPEED = 0.2;
    public static final double L1_GRABBER_SPEED = .15;
    public static final double L4_GRABBER_SPEED = .30;
    
    public static final double INTAKE_GRABBER_SPEED = 0.2;

    public static final double HOMED_CURRENT_DRAW = 60.0;

    public static final double MAX_ACCELERATION = 6000;
    public static final double MAX_VELOCITY = 15000;
    public static final double AT_SETPOINT_TOLERANCE = 1.0;

    public static final double ARBITRARY_FEEDFORWARD = 0.44;
}
