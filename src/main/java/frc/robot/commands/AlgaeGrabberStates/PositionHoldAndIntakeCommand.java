// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.AlgaeGrabberStates;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.AlgaeGrabberSubsystemConstants;
import frc.robot.subsystems.AlgaeGrabberSubsystem;
import frc.robot.subsystems.ElevatorSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class PositionHoldAndIntakeCommand extends Command {
  AlgaeGrabberSubsystem algaeGrabberSubsystem;
  ElevatorSubsystem elevatorSubsystem;
  
  double currentElevatorPosition = 5.0;
  double currentAlgaeGrabberPosition = 0.3;

  public PositionHoldAndIntakeCommand(AlgaeGrabberSubsystem algaeGrabberSubsystem, ElevatorSubsystem elevatorSubsystem) {
    this.algaeGrabberSubsystem = algaeGrabberSubsystem;
    this.elevatorSubsystem = elevatorSubsystem;

    addRequirements(algaeGrabberSubsystem, elevatorSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    this.currentElevatorPosition = elevatorSubsystem.getPosition();
    this.currentAlgaeGrabberPosition = algaeGrabberSubsystem.getLinearizedPosition();
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    System.out.println("Holding position and intaking");
    algaeGrabberSubsystem.setPosition(currentAlgaeGrabberPosition);
    elevatorSubsystem.setPosition(currentElevatorPosition);
    algaeGrabberSubsystem.setSpinMotor(AlgaeGrabberSubsystemConstants.INTAKE_MOTOR_SPEED);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    algaeGrabberSubsystem.stopAll();
    elevatorSubsystem.stopAll();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return algaeGrabberSubsystem.getSpinMotorCurrentDraw() > AlgaeGrabberSubsystemConstants.INTAKE_CURRENT_DRAW;
  }
}
