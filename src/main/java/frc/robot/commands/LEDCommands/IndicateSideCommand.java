// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.LEDCommands;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.LEDSubsystemConstants;
import frc.robot.subsystems.LEDSubsystem;

/* You should consider using the more terse Command factories API instead https://docs.wpilib.org/en/stable/docs/software/commandbased/organizing-command-based.html#defining-commands */
public class IndicateSideCommand extends Command {
  LEDSubsystem ledSubsystem;
  BooleanSupplier leftSideSelected;
  BooleanSupplier isInManualMode;
  public IndicateSideCommand(LEDSubsystem ledSubsystem, BooleanSupplier leftSideSelected, BooleanSupplier isInManualMode) {
    this.ledSubsystem = ledSubsystem;
    this.leftSideSelected = leftSideSelected;
    this.isInManualMode = isInManualMode;
    addRequirements(ledSubsystem);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {}

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    if(isInManualMode.getAsBoolean()) {
      LEDPattern p = LEDSubsystemConstants.MANUAL_MODE_ON;
      ledSubsystem.setMiddle(p);
      ledSubsystem.setRight(p);
      ledSubsystem.setLeft(p);
      ledSubsystem.updateBuffer();
    }

    if(leftSideSelected.getAsBoolean()) {
      ledSubsystem.leftOn();
      return;
    }
    ledSubsystem.rightOn();
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {}

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }
}
