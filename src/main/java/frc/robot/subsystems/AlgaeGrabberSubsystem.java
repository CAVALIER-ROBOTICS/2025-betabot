// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.AlgaeGrabberSubsystemConstants;
import frc.robot.Constants.RobotConstants;

public class AlgaeGrabberSubsystem extends SubsystemBase {
  SparkMax pivotMotor = new SparkMax(AlgaeGrabberSubsystemConstants.PIVOT_MOTOR_ID, MotorType.kBrushless);
  SparkMax spinMotor = new SparkMax(AlgaeGrabberSubsystemConstants.SPIN_MOTOR_ID, MotorType.kBrushless);

  DutyCycleEncoder thruBore = new DutyCycleEncoder(AlgaeGrabberSubsystemConstants.THRU_BORE_ENCODER_ID);

  PIDController controller = new PIDController(1.0, 0.5, 0);

  public AlgaeGrabberSubsystem() {
    SparkMaxConfig config = new SparkMaxConfig();
    config.voltageCompensation(RobotConstants.NOMINAL_VOLTAGE);
    config.idleMode(IdleMode.kBrake);

    pivotMotor.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
    spinMotor.configure(config, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

    controller.enableContinuousInput(0, 1); //Obviously, this will be an issue. That damn thru bore and its inability to count past one rotation.
    controller.setTolerance(.05);
  }

  public void setSpinMotor(double percent) {
    spinMotor.set(percent);
  }

  public void setPivotMotor(double percent) {
    pivotMotor.set(percent);
  }

  public void setPosition(double position) {
    double speed = controller.calculate(getPosition(), position);
    setPivotMotor(speed);
  }

  public void stopAll() {
    spinMotor.set(0.0);
    pivotMotor.set(0.0);
  }

  public double getPosition() {
    return thruBore.get();
  }

  public double getSpinMotorCurrentDraw() {
    return spinMotor.getOutputCurrent();
  }

  public boolean isAlgaeGrabberPIDAtSetpoint() {
    return controller.atSetpoint();
  }

  @Override
  public void periodic() {
    SmartDashboard.putNumber("AlgaeGrabberEncoder", getPosition());
    // This method will be called once per scheduler run
  }
}
