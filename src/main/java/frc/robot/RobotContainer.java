// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.function.BooleanSupplier;

import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.ConditionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;
import frc.robot.Constants.AlgaeGrabberSubsystemConstants;
import frc.robot.Constants.ElevatorSubsystemConstants;
import frc.robot.Constants.LEDSubsystemConstants;
import frc.robot.commands.FieldDriveCommand;
import frc.robot.commands.AlgaeGrabberStates.AlgaeGrabberGoToPositionCommand;
import frc.robot.commands.AlgaeGrabberStates.ElevatorPopUpAndAlgaeGrabberGoToPositionCommand;
import frc.robot.commands.AlgaeGrabberStates.PositionHoldAndEjectCommand;
import frc.robot.commands.AlgaeGrabberStates.UnsafeGroundIntakeCommand;
import frc.robot.commands.AlgaeGrabberStates.UnsafeProcessorScoreCommand;
import frc.robot.commands.AlgaeGrabberStates.AutonomousAlgaeGrabberCommands.AlgaeGrabberAndElevatorPositionAndIntakeCommand;
import frc.robot.commands.AutoAlign.AutoAlgaeCommand;
import frc.robot.commands.AutoAlign.AutoScoreCommand;
import frc.robot.commands.AutoAlign.AutoScoreL4Command;
import frc.robot.commands.ClimbStates.ClimbGoToJoystickSpeedCommand;
import frc.robot.commands.ElevatorStates.AutonomousElevatorCommands.ExtendToHeightThenScoreCommand;
import frc.robot.commands.Failsafes.OperatorFailsafeCommand;
import frc.robot.commands.LEDCommands.FullIndicateCommand;
import frc.robot.commands.LEDCommands.IndicateSideCommand;
import frc.robot.subsystems.AlgaeGrabberSubsystem;
import frc.robot.subsystems.ClimbSubsystem;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.ElevatorSubsystem;
import frc.robot.subsystems.LEDSubsystem;
import frc.robot.utils.PathLoader;
import frc.robot.commands.ElevatorStates.ElevatorRetractCommand;
import frc.robot.commands.ElevatorStates.ElevatorReturnToHomeAndZeroCommand;
import frc.robot.commands.ElevatorStates.ElevatorHPIntakeCommand;
import frc.robot.commands.ElevatorStates.ElevatorGoToPositionCommand;
import frc.robot.commands.SlowFieldDriveCommand;
import frc.robot.commands.HIDCommands.ControllerRumbleCommand;


public class RobotContainer {
  XboxController driver = new XboxController(0);
  XboxController operator = new XboxController(1);

  DriveSubsystem driveSubsystem = new DriveSubsystem();
  ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
  AlgaeGrabberSubsystem algaeGrabberSubsystem = new AlgaeGrabberSubsystem();
  LEDSubsystem ledSubsystem = new LEDSubsystem();
  // ClimbSubsystem climbSubsystem = new ClimbSubsystem();

  Command defaultDriveCommand = new FieldDriveCommand(driveSubsystem, driver::getLeftX, driver::getLeftY, driver::getRightX);
  Command algaeGrabberDefaultCommand = new AlgaeGrabberGoToPositionCommand(algaeGrabberSubsystem, AlgaeGrabberSubsystemConstants.RETRACTED_ENCODER_POSITION);
  Command defaultLEDSubsystemCommand = new IndicateSideCommand(ledSubsystem, () -> getScoringOnLeft(), () -> getManualOverride());

  Command intakeCommand = new ElevatorHPIntakeCommand(elevatorSubsystem);

  Command homeElevatorAndDontBreakAlgaeGrabber = new SequentialCommandGroup(
    new ElevatorPopUpAndAlgaeGrabberGoToPositionCommand(algaeGrabberSubsystem, elevatorSubsystem, AlgaeGrabberSubsystemConstants.RETRACTED_ENCODER_POSITION),
    new ElevatorRetractCommand(elevatorSubsystem).alongWith(new AlgaeGrabberGoToPositionCommand(algaeGrabberSubsystem, AlgaeGrabberSubsystemConstants.RETRACTED_ENCODER_POSITION))
  );

  Command safeCurrentHomeCommand = new SequentialCommandGroup(
    // new ElevatorPopUpAndAlgaeGrabberGoToPositionCommand(algaeGrabberSubsystem, elevatorSubsystem, AlgaeGrabberSubsystemConstants.RETRACTED_ENCODER_POSITION),
    new ElevatorReturnToHomeAndZeroCommand(elevatorSubsystem)
    // new ElevatorRetractCommand(elevatorSubsystem).alongWith(new AlgaeGrabberGoToPositionCommand(algaeGrabberSubsystem, AlgaeGrabberSubsystemConstants.RETRACTED_ENCODER_POSITION))
  );

  boolean scoringOnLeft = true;
  boolean ejectAlgae = true;
  boolean isManuallyOverridden = false;

  public RobotContainer() {
    configureNamedCommands();
    PathLoader.configureAutoBuilder(driveSubsystem, driveSubsystem.getPoseEstimator());
    configureDefaultBindings();
    configureBindings();
  }

  private void configureNamedCommands() {
    NamedCommands.registerCommand("ScoreL4", new ExtendToHeightThenScoreCommand(elevatorSubsystem, ElevatorSubsystemConstants.L4_ENCODER_POSITION, ElevatorSubsystemConstants.AUTO_L4_GRABBER_SPEED).withTimeout(2.5));
    NamedCommands.registerCommand("ScoreL3", new ExtendToHeightThenScoreCommand(elevatorSubsystem, ElevatorSubsystemConstants.L3_ENCODER_POSITION).withTimeout(2));
    NamedCommands.registerCommand("ScoreL2", new ExtendToHeightThenScoreCommand(elevatorSubsystem, ElevatorSubsystemConstants.L2_ENCODER_POSITION).withTimeout(1));
    // NamedCommands.registerCommand("ScoreL4", new ExtendToHeightThenScoreCommand(elevatorSubsystem, ElevatorSubsystemConstants.L2_ENCODER_POSITION).withTimeout(2.5));

    NamedCommands.registerCommand("HPIntake", intakeCommand);
  }

  private void configureBindings() {
    configureDriveBindings();
    configureElevatorBindings();
    configureAlgaeGrabberBindings();
    configureSideSelectorBindings();
    // configureAlgaeEjectOrRetainBindings();
    congigureManualOverrideBindings();
  }

  private void configureDefaultBindings() {
    driveSubsystem.setDefaultCommand(defaultDriveCommand);
    algaeGrabberSubsystem.setDefaultCommand(algaeGrabberDefaultCommand);
    elevatorSubsystem.setDefaultCommand(new ElevatorRetractCommand(elevatorSubsystem));
    ledSubsystem.setDefaultCommand(defaultLEDSubsystemCommand);
    // climbSubsystem.setDefaultCommand(new ClimbGoToJoystickSpeedCommand(climbSubsystem, operator::getRightY));
  }

  private void configureDriveBindings() {
    JoystickButton zeroDriverGyro = new JoystickButton(driver, 4);
    zeroDriverGyro.onTrue(new InstantCommand(driveSubsystem::driverGyroZero));
  }

  private void configureElevatorBindings() {
    POVButton l2Score = new POVButton(driver, 90);
    POVButton l3Score = new POVButton(driver, 0);
    POVButton l4Score = new POVButton(driver, 270);
    POVButton scoreCancel = new POVButton(driver, 180);

    BooleanSupplier scoringOnLeftBooleanSupplier = this::getScoringOnLeft;
    BooleanSupplier runElevatorExtruder = () -> driver.getRightTriggerAxis() > .25;
    BooleanSupplier isManuallyOverridenBooleanSupplier = this::getManualOverride;

    Command autoL2 = new AutoScoreCommand(driveSubsystem, elevatorSubsystem, ElevatorSubsystemConstants.L2_ENCODER_POSITION, scoringOnLeftBooleanSupplier);
    Command autoL3 = new AutoScoreCommand(driveSubsystem, elevatorSubsystem, ElevatorSubsystemConstants.L3_ENCODER_POSITION, scoringOnLeftBooleanSupplier);
    Command autoL4 = new AutoScoreL4Command(driveSubsystem, elevatorSubsystem, ElevatorSubsystemConstants.L4_ENCODER_POSITION, scoringOnLeftBooleanSupplier, ElevatorSubsystemConstants.L4_GRABBER_SPEED);

    ParallelCommandGroup l2CommandManual = new ParallelCommandGroup(
      new ElevatorGoToPositionCommand(elevatorSubsystem, runElevatorExtruder, ElevatorSubsystemConstants.L2_ENCODER_POSITION),
      new SlowFieldDriveCommand(driveSubsystem, driver::getLeftX, driver::getLeftY, driver::getRightX)
    );

    ParallelCommandGroup l3CommandManual = new ParallelCommandGroup(
      new ElevatorGoToPositionCommand(elevatorSubsystem, runElevatorExtruder, ElevatorSubsystemConstants.L3_ENCODER_POSITION),
      new SlowFieldDriveCommand(driveSubsystem, driver::getLeftX, driver::getLeftY, driver::getRightX)
    );

    ParallelCommandGroup l4CommandManual = new ParallelCommandGroup(
      new ElevatorGoToPositionCommand(elevatorSubsystem, runElevatorExtruder, ElevatorSubsystemConstants.L4_ENCODER_POSITION),
      new SlowFieldDriveCommand(driveSubsystem, driver::getLeftX, driver::getLeftY, driver::getRightX)
    );

    l2Score.onTrue(new ConditionalCommand(l2CommandManual, autoL2, isManuallyOverridenBooleanSupplier));
    l3Score.onTrue(new ConditionalCommand(l3CommandManual, autoL3, isManuallyOverridenBooleanSupplier));
    l4Score.onTrue(new ConditionalCommand(l4CommandManual, autoL4, isManuallyOverridenBooleanSupplier));

    scoreCancel.onTrue(homeElevatorAndDontBreakAlgaeGrabber);

    JoystickButton hpIntakeButton = new JoystickButton(driver, 6);
    hpIntakeButton.toggleOnTrue(intakeCommand);
  }

  private void configureAlgaeGrabberBindings() {
    POVButton highAlgae = new POVButton(operator, 0);
    POVButton lowAlgae = new POVButton(operator, 90);
    POVButton cancelAlgaeGrab = new POVButton(operator, 180);

    // BooleanSupplier ejectAlgaeBooleanSupplier = this::getEjectAlgae;
    BooleanSupplier runOuttakeBooleanSupplier = () -> operator.getRightTriggerAxis() > .25;

    Command highGrab = new AlgaeGrabberAndElevatorPositionAndIntakeCommand(elevatorSubsystem, algaeGrabberSubsystem, ElevatorSubsystemConstants.HIGH_ALGAE_POSITION, AlgaeGrabberSubsystemConstants.ALGAE_REMOVAL_ENCODER_POSITION);
    Command lowGrab = new AlgaeGrabberAndElevatorPositionAndIntakeCommand(elevatorSubsystem, algaeGrabberSubsystem, ElevatorSubsystemConstants.LOW_ALGAE_POSITION, AlgaeGrabberSubsystemConstants.ALGAE_REMOVAL_ENCODER_POSITION);

    highAlgae.onTrue(
      new ParallelCommandGroup(
        new SequentialCommandGroup(highGrab, new PositionHoldAndEjectCommand(algaeGrabberSubsystem, elevatorSubsystem, runOuttakeBooleanSupplier)),
        new FieldDriveCommand(driveSubsystem, driver::getLeftX, driver::getLeftY, driver::getRightX)
      )
    );

    lowAlgae.onTrue(
      new ParallelCommandGroup(
        new SequentialCommandGroup(lowGrab, new PositionHoldAndEjectCommand(algaeGrabberSubsystem, elevatorSubsystem, runOuttakeBooleanSupplier)),
        new FieldDriveCommand(driveSubsystem, driver::getLeftX, driver::getLeftY, driver::getRightX)
      )
    );

    cancelAlgaeGrab.onTrue(homeElevatorAndDontBreakAlgaeGrabber);

    JoystickButton intakeAlgae = new JoystickButton(driver, 1);
    intakeAlgae.onTrue(new AutoAlgaeCommand(driveSubsystem, elevatorSubsystem, algaeGrabberSubsystem, runOuttakeBooleanSupplier, driver::getLeftX, driver::getLeftY, driver::getRightX));
    intakeAlgae.onTrue(new ControllerRumbleCommand(operator).withTimeout(0.25));

    JoystickButton processorScore = new JoystickButton(operator, 6);
    processorScore.onTrue(
      new SequentialCommandGroup(
        new ElevatorPopUpAndAlgaeGrabberGoToPositionCommand(algaeGrabberSubsystem, elevatorSubsystem, AlgaeGrabberSubsystemConstants.PROCESSOR_SCORING_ENCODER_POSITION), //Hop grabber over fleft module
        new UnsafeProcessorScoreCommand(algaeGrabberSubsystem, elevatorSubsystem, runOuttakeBooleanSupplier), //Run Intake
        new ElevatorPopUpAndAlgaeGrabberGoToPositionCommand(algaeGrabberSubsystem, elevatorSubsystem, AlgaeGrabberSubsystemConstants.RETRACTED_ENCODER_POSITION) //Stow algae
      )
    );

    JoystickButton groundIntake = new JoystickButton(operator, 5);
    groundIntake.onTrue(
      new SequentialCommandGroup(
        new ElevatorPopUpAndAlgaeGrabberGoToPositionCommand(algaeGrabberSubsystem, elevatorSubsystem, AlgaeGrabberSubsystemConstants.GROUND_INTAKE_ENCODER_POSITION), //Hop grabber over fleft module
        new UnsafeGroundIntakeCommand(algaeGrabberSubsystem, elevatorSubsystem), //Run Intake
        new ElevatorPopUpAndAlgaeGrabberGoToPositionCommand(algaeGrabberSubsystem, elevatorSubsystem, AlgaeGrabberSubsystemConstants.PROCESSOR_SCORING_ENCODER_POSITION) //Hop grabber over fleft module
      )
    );
  }

  // private void configureClimbBindings() {
  //   JoystickButton climb = new JoystickButton(operator, 2);
  //   Command climbCommand = new SequentialCommandGroup(
  //     new ElevatorPopUpAndAlgaeGrabberGoToPositionCommand(algaeGrabberSubsystem, elevatorSubsystem, AlgaeGrabberSubsystemConstants.PROCESSOR_SCORING_ENCODER_POSITION),
  //     new ClimbGoToJoystickSpeedCommand(climbSubsystem, operator::getLeftY).alongWith(new AlgaeGrabberGoToPositionCommand(algaeGrabberSubsystem, AlgaeGrabberSubsystemConstants.PROCESSOR_SCORING_ENCODER_POSITION))
  //   ).raceWith(new FullIndicateCommand(ledSubsystem, LEDSubsystemConstants.CLIMBING_MODE_ON));

  //   climb.onTrue(climbCommand);
  // }

  private void configureSideSelectorBindings() {
    JoystickButton leftSelector = new JoystickButton(driver, 3);
    JoystickButton rightSelector = new JoystickButton(driver, 2);

    leftSelector.onTrue(new InstantCommand(() -> {
      System.out.println("Left selector pressed");
      scoringOnLeft = true;
    }));
    rightSelector.onTrue(new InstantCommand(() -> {scoringOnLeft = false;}));
  }

  private void configureAlgaeEjectOrRetainBindings() {
    JoystickButton ejectAlgaeButton = new JoystickButton(operator, 2);
    JoystickButton retainAlgaeButton = new JoystickButton(operator, 3);

    ejectAlgaeButton.onTrue(new InstantCommand(() -> {
      System.out.println("Ejecting");
      ejectAlgae = true;
    }));

    retainAlgaeButton.onTrue(new InstantCommand(() -> {
      System.out.println("Retaining");
      ejectAlgae = false;
    }));
  }

  private void congigureManualOverrideBindings() {
    JoystickButton manualOverrideOn = new JoystickButton(operator, 8);
    JoystickButton manualOverrideOff = new JoystickButton(operator, 7);
    JoystickButton currentHomeElevator = new JoystickButton(operator, 4);

    JoystickButton failsafeButton = new JoystickButton(operator, 1);

    manualOverrideOn.onTrue(new InstantCommand(() -> {
      isManuallyOverridden = true;
      System.out.println("Manual override on");
    }));

    manualOverrideOff.onTrue(new InstantCommand(() -> {
      isManuallyOverridden = false;
      System.out.println("Manual override off");
    }));

    failsafeButton.toggleOnTrue(new OperatorFailsafeCommand(
      elevatorSubsystem,
      algaeGrabberSubsystem,
      operator::getLeftY,
      operator::getRightY,
      () -> operator.getRightTriggerAxis() > .25
    ).raceWith(
      new FullIndicateCommand(ledSubsystem, LEDSubsystemConstants.OPERATOR_FINAL_FAILSAFE_ON)
    ));

    currentHomeElevator.onTrue(safeCurrentHomeCommand);
  }

  public boolean getScoringOnLeft() {
    return scoringOnLeft;
  }

  public boolean getEjectAlgae() {
    return ejectAlgae;
  }

  public boolean getManualOverride() {
    return isManuallyOverridden;
  }

  public Command getAutonomousCommand() {
    return Commands.print("No autonomous command configured");
  }
}
