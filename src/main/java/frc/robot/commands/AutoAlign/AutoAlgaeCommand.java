// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.AutoAlign;

import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.AlgaeGrabberSubsystem;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.utils.AutoAlignCommandFactory;
import frc.robot.utils.PathLoader;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class AutoAlgaeCommand extends Command {
  DriveSubsystem driveSubsystem;
  ElevatorSubsystem elevatorSubsystem;
  AlgaeGrabberSubsystem algaeGrabberSubsystem;
  BooleanSupplier runExtruderBooleanSupplier;

  DoubleSupplier x;
  DoubleSupplier y;
  DoubleSupplier rot;

  public AutoAlgaeCommand(DriveSubsystem driveSubsystem, ElevatorSubsystem elevatorSubsystem, AlgaeGrabberSubsystem algaeGrabberSubsystem, BooleanSupplier runExtruderBooleanSupplier, DoubleSupplier x, DoubleSupplier y, DoubleSupplier rot) {
    this.elevatorSubsystem = elevatorSubsystem;
    this.driveSubsystem = driveSubsystem;
    this.algaeGrabberSubsystem = algaeGrabberSubsystem;
    this.runExtruderBooleanSupplier = runExtruderBooleanSupplier;
    this.x = x;
    this.y = y;
    this.rot = rot;

    addRequirements(driveSubsystem, algaeGrabberSubsystem, elevatorSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    System.out.println("Running auto algae intake");
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {}

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    Command algaeIntakeCommand = AutoAlignCommandFactory.getSafeAutoAlignAlgaeIntakeWithSlowDriveV2(
      driveSubsystem.getPoseEstimator().getPose2d(),
      elevatorSubsystem,
      algaeGrabberSubsystem,
      driveSubsystem,
      PathLoader.getShouldFlipPath(),
      x,
      y,
      rot,
      runExtruderBooleanSupplier
    );
    algaeIntakeCommand.schedule();
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return true;
  }
}
