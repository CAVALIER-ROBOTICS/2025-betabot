// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants.AlgaeGrabberSubsystemConstants;
import frc.robot.Constants.ElevatorSubsystemConstants;
import frc.robot.Constants.PathingConstants;
import frc.robot.commands.DriveAtChassisSpeedsCommand;
import frc.robot.commands.FieldDriveCommand;
import frc.robot.commands.AlgaeGrabberStates.EjectAlgaeCommand;
import frc.robot.commands.AlgaeGrabberStates.PositionHoldAndEjectCommand;
import frc.robot.commands.AlgaeGrabberStates.PositionHoldCommand;
import frc.robot.commands.AlgaeGrabberStates.StowAlgaeCommand;
import frc.robot.commands.AlgaeGrabberStates.AutonomousAlgaeGrabberCommands.AlgaeGrabberAndElevatorPositionAndIntakeCommand;
import frc.robot.commands.AutoAlign.FollowPrecisePathAndRaiseElevatorAndScoreCommand;
import frc.robot.commands.AutoAlign.FollowPrecisePathCommand;
import frc.robot.commands.ElevatorStates.ElevatorGoToPositionAndEndCommand;
import frc.robot.commands.ElevatorStates.ElevatorRunGrabberAndGoToPositionCommand;
import frc.robot.commands.ElevatorStates.AutonomousElevatorCommands.ExtendToHeightThenScoreCommand;
import frc.robot.subsystems.AlgaeGrabberSubsystem;
import frc.robot.subsystems.DriveSubsystem;
import frc.robot.subsystems.ElevatorSubsystem;

/** Add your docs here. */
public class AutoAlignCommandFactory {
    static List<Pose2d> leftBlueAllianceScoringPositions = new ArrayList<>();
    static List<Pose2d> rightBlueAllianceScoringPositions = new ArrayList<>();

    static List<Pose2d> rightRedAllianceScoringPositions = new ArrayList<>();
    static List<Pose2d> leftRedAllianceScoringPositions = new ArrayList<>();

    static List<Pose2d> blueAllianceAlgaePositions = new ArrayList<>();
    static List<Pose2d> redAllianceAlgaePositions = new ArrayList<>();

    static List<Pose2d> leftBlueL4Positions = new ArrayList<>();
    static List<Pose2d> rightBlueL4Positions = new ArrayList<>();

    static List<Pose2d> leftRedL4Positions = new ArrayList<>();
    static List<Pose2d> rightRedL4Positions = new ArrayList<>();

    static boolean initialized = false;

    public static List<Pose2d> mirrorBlueSidedPoseList(List<Pose2d> list) {
        List<Pose2d> ret = new ArrayList<>();
        for(Pose2d pose: list) {
            Pose2d poseToAdd = new Pose2d(PathingConstants.FIELD_WIDTH_METERS - pose.getX(), PathingConstants.FIELD_HEIGHT_METERS - pose.getY(), pose.getRotation().plus(Rotation2d.fromDegrees(180)));
            ret.add(poseToAdd);
        }
        return ret;
    }

    @SuppressWarnings("unused")
    private static void display(List<Pose2d> list) {
        for(int i = 0; i < list.size(); i++) {
            Field2d field = new Field2d();
            field.setRobotPose(list.get(i));
            SmartDashboard.putData(String.valueOf(i), field);
        }
    }

    public static List<Pose2d> applyXYOffsetsToPoseList(double x, double y, List<Pose2d> originalPoseList) {
        List<Pose2d> ret = new ArrayList<>();
        for(int i = 0; i < originalPoseList.size(); i++) {
            // Field2d field = new Field2d();
            Pose2d originalPose = originalPoseList.get(i);
            double offset = Math.atan2(x, y);
            double magOffset = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
            double poseOrientationRadians = originalPose.getRotation().getRadians();

            double newX = originalPose.getX() + ((Math.cos(poseOrientationRadians + offset) * magOffset));
            double newY = originalPose.getY() + ((Math.sin(poseOrientationRadians + offset) * magOffset));

            Pose2d np = new Pose2d(newX, newY, originalPose.getRotation());

            // field.setRobotPose(np);
            // SmartDashboard.putData(String.valueOf(i), field);

            ret.add(np);
        }
        return ret;
    }

    public static void initalize() {
        if(!initialized) {
            initialized = true;
            leftBlueAllianceScoringPositions = applyXYOffsetsToPoseList(PathingConstants.X_OFFSET, PathingConstants.Y_OFFSET, PathingConstants.LEFT_BLUE_SIDED_SCORING_POSITIONS);
            rightBlueAllianceScoringPositions = applyXYOffsetsToPoseList(PathingConstants.X_OFFSET, PathingConstants.Y_OFFSET, PathingConstants.RIGHT_BLUE_SIDED_SCORING_POSITIONS);

            leftRedAllianceScoringPositions = mirrorBlueSidedPoseList(leftBlueAllianceScoringPositions);
            rightRedAllianceScoringPositions = mirrorBlueSidedPoseList(rightBlueAllianceScoringPositions);

            blueAllianceAlgaePositions = applyXYOffsetsToPoseList(PathingConstants.ALGAE_X_OFFSET, PathingConstants.ALGAE_Y_OFFSET, PathingConstants.BLUE_SIDED_ALGAE_INTAKE_POSITIONS);
            redAllianceAlgaePositions = mirrorBlueSidedPoseList(blueAllianceAlgaePositions);

            leftBlueL4Positions = applyXYOffsetsToPoseList(PathingConstants.L4_X_OFFSET, PathingConstants.L4_Y_OFFSET, PathingConstants.LEFT_BLUE_SIDED_SCORING_POSITIONS);
            rightBlueL4Positions = applyXYOffsetsToPoseList(PathingConstants.L4_X_OFFSET, PathingConstants.L4_Y_OFFSET, PathingConstants.RIGHT_BLUE_SIDED_SCORING_POSITIONS);
            
            leftRedL4Positions = mirrorBlueSidedPoseList(leftBlueL4Positions);
            rightRedL4Positions = mirrorBlueSidedPoseList(rightBlueL4Positions);

            // display(leftRedL4Positions);
        }
    }

    public static Pose2d getClosestPose(Pose2d origin, boolean onRedAlliance, boolean left) {
        initalize();
        List<Pose2d> poseList;

        if(left) { //I'm sorry
            if(onRedAlliance) {
                poseList = leftRedAllianceScoringPositions;
            } else {
                poseList = leftBlueAllianceScoringPositions;
            }
        } else {
            if(onRedAlliance) {
                poseList = rightRedAllianceScoringPositions;
            } else {
                poseList = rightBlueAllianceScoringPositions;
            }
        }

        return origin.nearest(poseList);
    }

    public static Pose2d getClosestL4Pose(Pose2d origin, boolean onRedAlliance, boolean left) {
        initalize();
        List<Pose2d> poseList;

        if(left) { //I'm sorry
            if(onRedAlliance) {
                poseList = leftRedL4Positions;
            } else {
                poseList = leftBlueL4Positions;
            }
        } else {
            if(onRedAlliance) {
                poseList = rightRedL4Positions;
            } else {
                poseList = rightBlueL4Positions;
            }
        }

        return origin.nearest(poseList);
    }

    private static void test(Pose2d p1, Pose2d p2) {
        Field2d f1 = new Field2d();
        Field2d f2 = new Field2d();

        f1.setRobotPose(p1);
        f2.setRobotPose(p2);

        SmartDashboard.putData("currentPose", f1);
        SmartDashboard.putData("goalPose", f2);
    }

    public static boolean isPoseSafeToDriveTo(Pose2d currentPose, Pose2d goalPose) {
        test(currentPose, goalPose);
        double distSquared = Math.pow(currentPose.getX() - goalPose.getX(), 2) + Math.pow(currentPose.getY() - goalPose.getY(), 2);
        return Math.sqrt(distSquared) < PathingConstants.MAXIMUM_PATHING_DISTANCE;
    }

    public static Pose2d getClosestAlgaeIntakePose(Pose2d origin, boolean onRedAlliance) {
        List<Pose2d> poseList = (onRedAlliance) ? redAllianceAlgaePositions: blueAllianceAlgaePositions;
        return origin.nearest(poseList);
    }

    public static double getAlgaeElevatorEncoderPosition(Pose2d origin, boolean onRedAlliance) {
        Pose2d nearestPose = getClosestAlgaeIntakePose(origin, onRedAlliance);
        int index = (onRedAlliance) ? redAllianceAlgaePositions.indexOf(nearestPose): blueAllianceAlgaePositions.indexOf(nearestPose);
        boolean isHigh = index % 2 == 0; //This works because I found the poses in a circle and the first pose I found was high. It's stupid.

        return (isHigh) ? ElevatorSubsystemConstants.HIGH_ALGAE_POSITION: ElevatorSubsystemConstants.LOW_ALGAE_POSITION;
    }

    public static Command getAutoAlignDriveCommand(DriveSubsystem driveSubsystem, Pose2d currentPosition, boolean onRedAlliance, boolean onLeftSide) {
        initalize();
        Pose2d goalPose = getClosestPose(currentPosition, onRedAlliance, onLeftSide);

        return new FollowPrecisePathCommand(driveSubsystem, goalPose);
    }

    public static Command getAutoAlignDriveCommandL4(DriveSubsystem driveSubsystem, Pose2d currentPosition, boolean onRedAlliance, boolean onLeftSide) {
        initalize();
        Pose2d goalPose = getClosestL4Pose(currentPosition, onRedAlliance, onLeftSide);

        return new FollowPrecisePathCommand(driveSubsystem, goalPose);
    }

    public static Command getAutoAlignDriveCommandAlgae(DriveSubsystem driveSubsystem, Pose2d currentPosition, Pose2d goalPose, boolean onRedAlliance) {
        return new FollowPrecisePathCommand(driveSubsystem, goalPose);
    }

    public static Command getAutoAlignAndScoreCommand(Pose2d currentPosition, ElevatorSubsystem elevatorSubsystem, DriveSubsystem driveSubsystem, double elevatorEncoderPosition, boolean onRedAlliance, boolean onLeftSide) {
        System.out.println(onLeftSide);
        return new SequentialCommandGroup(
            getAutoAlignDriveCommand(driveSubsystem, currentPosition, onRedAlliance, onLeftSide),
            new ExtendToHeightThenScoreCommand(elevatorSubsystem, elevatorEncoderPosition)
        ).onlyIf(() -> isPoseSafeToDriveTo(currentPosition, getClosestPose(currentPosition, onRedAlliance, onLeftSide)));
    }

    public static Command getAutoAlignAndScoreCommand(Pose2d currentPosition, ElevatorSubsystem elevatorSubsystem, DriveSubsystem driveSubsystem, double elevatorEncoderPosition, boolean onRedAlliance, boolean onLeftSide, double grabberSpeed) {
        System.out.println(onLeftSide);
        return new SequentialCommandGroup(
            getAutoAlignDriveCommand(driveSubsystem, currentPosition, onRedAlliance, onLeftSide),
            new ExtendToHeightThenScoreCommand(elevatorSubsystem, elevatorEncoderPosition, grabberSpeed)
        ).onlyIf(() -> isPoseSafeToDriveTo(currentPosition, getClosestPose(currentPosition, onRedAlliance, onLeftSide)));
    }

    public static Command getAutoAlignAndScoreCommandParallel(Pose2d currentPosition, ElevatorSubsystem elevatorSubsystem, DriveSubsystem driveSubsystem, double elevatorEncoderPosition, boolean onRedAlliance, boolean onLeftSide) {
        System.out.println(onLeftSide);
        return new SequentialCommandGroup(
            new ParallelCommandGroup(
                getAutoAlignDriveCommand(driveSubsystem, currentPosition, onRedAlliance, onLeftSide),
                new ElevatorGoToPositionAndEndCommand(elevatorSubsystem, elevatorEncoderPosition)
            ),
            new ElevatorRunGrabberAndGoToPositionCommand(elevatorSubsystem, elevatorEncoderPosition)
        ).onlyIf(() -> isPoseSafeToDriveTo(currentPosition, getClosestPose(currentPosition, onRedAlliance, onLeftSide)));
    }

    public static Command getAutoAlignAndScoreCommandParallel(Pose2d currentPosition, ElevatorSubsystem elevatorSubsystem, DriveSubsystem driveSubsystem, double elevatorEncoderPosition, boolean onRedAlliance, boolean onLeftSide, double grabberSpeed) {
        System.out.println(onLeftSide);
        return new SequentialCommandGroup(
            new ParallelCommandGroup(
                getAutoAlignDriveCommand(driveSubsystem, currentPosition, onRedAlliance, onLeftSide),
                new ElevatorGoToPositionAndEndCommand(elevatorSubsystem, elevatorEncoderPosition)
            ),
            new ElevatorRunGrabberAndGoToPositionCommand(elevatorSubsystem, elevatorEncoderPosition, grabberSpeed)
        ).onlyIf(() -> isPoseSafeToDriveTo(currentPosition, getClosestPose(currentPosition, onRedAlliance, onLeftSide)));
    }

    public static Command getAutoAlignAndScoreCommandParallelV2(Pose2d currentPosition, ElevatorSubsystem elevatorSubsystem, DriveSubsystem driveSubsystem, double elevatorEncoderPosition, boolean onRedAlliance, boolean onLeftSide) {
        initalize();
        Pose2d goalPose = getClosestPose(currentPosition, onRedAlliance, onLeftSide);

        return new FollowPrecisePathAndRaiseElevatorAndScoreCommand(driveSubsystem, elevatorSubsystem, elevatorEncoderPosition, goalPose).onlyIf(() -> isPoseSafeToDriveTo(currentPosition, goalPose));
    }

    public static Command getAutoAlignAndScoreCommandParallelV2(Pose2d currentPosition, ElevatorSubsystem elevatorSubsystem, DriveSubsystem driveSubsystem, double elevatorEncoderPosition, boolean onRedAlliance, boolean onLeftSide, double grabberSpeed) {
        initalize();
        Pose2d goalPose = getClosestPose(currentPosition, onRedAlliance, onLeftSide);

        return new FollowPrecisePathAndRaiseElevatorAndScoreCommand(driveSubsystem, elevatorSubsystem, elevatorEncoderPosition, goalPose, grabberSpeed).onlyIf(() -> isPoseSafeToDriveTo(currentPosition, goalPose));
    }

    public static Command getAutonomousAutoAlign(Pose2d currentPosition, ElevatorSubsystem elevatorSubsystem, DriveSubsystem driveSubsystem, double elevatorEncoderPosition, boolean onRedAlliance, boolean onLeftSide, double grabberSpeed) {
        initalize();
        Pose2d goalPose = getClosestPose(currentPosition, onRedAlliance, onLeftSide);

        return new FollowPrecisePathAndRaiseElevatorAndScoreCommand(driveSubsystem, elevatorSubsystem, elevatorEncoderPosition, goalPose, grabberSpeed).withTimeout(3).onlyIf(() -> isPoseSafeToDriveTo(currentPosition, goalPose));
    }

    public static Command getAutoAlignAndAlgaeIntakeParallel(Pose2d currentPosition, ElevatorSubsystem elevatorSubsystem, AlgaeGrabberSubsystem algaeGrabberSubsystem, DriveSubsystem driveSubsystem, boolean onRedAlliance, boolean ejectAfterIntaking) {
        initalize();
        Pose2d goalPose = getClosestAlgaeIntakePose(currentPosition, onRedAlliance);
        double elevatorEncoderPosition = getAlgaeElevatorEncoderPosition(goalPose, onRedAlliance);

        Command driveAndIntakeNoEndState = new ParallelCommandGroup(
            getAutoAlignDriveCommandAlgae(driveSubsystem, currentPosition, goalPose, onRedAlliance),
            new AlgaeGrabberAndElevatorPositionAndIntakeCommand(elevatorSubsystem, algaeGrabberSubsystem, elevatorEncoderPosition, AlgaeGrabberSubsystemConstants.ALGAE_REMOVAL_ENCODER_POSITION) 
        );

        Command driveAndIntake;

        if(ejectAfterIntaking) {
            driveAndIntake = driveAndIntakeNoEndState.andThen(new EjectAlgaeCommand(algaeGrabberSubsystem, elevatorSubsystem));
        } else {
            driveAndIntake = driveAndIntakeNoEndState.andThen(new StowAlgaeCommand(algaeGrabberSubsystem, elevatorSubsystem));
        }

        return driveAndIntake.onlyIf(() -> isPoseSafeToDriveTo(currentPosition, goalPose));
    }

    public static Command getSafeAutoAlignAlgaeIntake(Pose2d currentPosition, ElevatorSubsystem elevatorSubsystem, AlgaeGrabberSubsystem algaeGrabberSubsystem, DriveSubsystem driveSubsystem, boolean onRedAlliance, boolean ejectAfterIntaking) {
        initalize();
        Pose2d goalPose = getClosestAlgaeIntakePose(currentPosition, onRedAlliance);
        double elevatorEncoderPosition = getAlgaeElevatorEncoderPosition(goalPose, onRedAlliance);
        
        Command driveAndIntakeNoFinalState = new SequentialCommandGroup(
            getAutoAlignDriveCommandAlgae(driveSubsystem, currentPosition, goalPose, onRedAlliance),
            new AlgaeGrabberAndElevatorPositionAndIntakeCommand(elevatorSubsystem, algaeGrabberSubsystem, elevatorEncoderPosition, AlgaeGrabberSubsystemConstants.ALGAE_REMOVAL_ENCODER_POSITION)
            .raceWith(
                new DriveAtChassisSpeedsCommand(driveSubsystem, AlgaeGrabberSubsystemConstants.INTAKE_CHASSIS_SPEEDS)
            ),
            new ParallelCommandGroup(
                new PositionHoldCommand(algaeGrabberSubsystem, elevatorSubsystem),
                new DriveAtChassisSpeedsCommand(driveSubsystem, AlgaeGrabberSubsystemConstants.RETRACT_CHASSIS_SPEEDS)
            ).withTimeout(0.25)
        );

        Command driveAndIntake;

        if(ejectAfterIntaking) {
            driveAndIntake = driveAndIntakeNoFinalState.andThen(new EjectAlgaeCommand(algaeGrabberSubsystem, elevatorSubsystem));
        } else {
            driveAndIntake = driveAndIntakeNoFinalState.andThen(new StowAlgaeCommand(algaeGrabberSubsystem, elevatorSubsystem));
        }

        return driveAndIntake.onlyIf(() -> isPoseSafeToDriveTo(currentPosition, goalPose));
    }

    public static Command getSafeAutoAlignAlgaeIntakeWithSlowDrive(Pose2d currentPosition, ElevatorSubsystem elevatorSubsystem, AlgaeGrabberSubsystem algaeGrabberSubsystem, DriveSubsystem driveSubsystem, boolean onRedAlliance, DoubleSupplier x, DoubleSupplier y, DoubleSupplier rot, BooleanSupplier eject) {
        initalize();
        Pose2d goalPose = getClosestAlgaeIntakePose(currentPosition, onRedAlliance);
        double elevatorEncoderPosition = getAlgaeElevatorEncoderPosition(goalPose, onRedAlliance);

        Command driveAndIntake = new SequentialCommandGroup(
            getAutoAlignDriveCommandAlgae(driveSubsystem, currentPosition, goalPose, onRedAlliance),
            new AlgaeGrabberAndElevatorPositionAndIntakeCommand(elevatorSubsystem, algaeGrabberSubsystem, elevatorEncoderPosition, AlgaeGrabberSubsystemConstants.ALGAE_REMOVAL_ENCODER_POSITION)
            .raceWith(
                new DriveAtChassisSpeedsCommand(driveSubsystem, AlgaeGrabberSubsystemConstants.INTAKE_CHASSIS_SPEEDS).beforeStarting(new WaitCommand(.5))
            )
        );

        return driveAndIntake.andThen(new ParallelCommandGroup(
            new PositionHoldAndEjectCommand(algaeGrabberSubsystem, elevatorSubsystem, eject),
            new FieldDriveCommand(driveSubsystem, x, y, rot)
        )).onlyIf(() -> isPoseSafeToDriveTo(currentPosition, goalPose));
    }

    public static Command getL4AutoAlignCommand(Pose2d currentPosition, ElevatorSubsystem elevatorSubsystem, DriveSubsystem driveSubsystem, double elevatorEncoderPosition, boolean onRedAlliance, boolean onLeftSide, double grabberSpeed) {
        return new SequentialCommandGroup(
            getAutoAlignDriveCommandL4(driveSubsystem, currentPosition, onRedAlliance, onLeftSide),
            new ExtendToHeightThenScoreCommand(elevatorSubsystem, elevatorEncoderPosition, grabberSpeed)
        ).onlyIf(() -> isPoseSafeToDriveTo(currentPosition, getClosestPose(currentPosition, onRedAlliance, onLeftSide)));
    }

    public static Command getL4AutoAlignCommandParallel(Pose2d currentPosition, ElevatorSubsystem elevatorSubsystem, DriveSubsystem driveSubsystem, double elevatorEncoderPosition, boolean onRedAlliance, boolean onLeftSide, double grabberSpeed) {
        return new ParallelCommandGroup(
            getAutoAlignDriveCommandL4(driveSubsystem, currentPosition, onRedAlliance, onLeftSide),
            new ExtendToHeightThenScoreCommand(elevatorSubsystem, elevatorEncoderPosition, grabberSpeed)
        ).onlyIf(() -> isPoseSafeToDriveTo(currentPosition, getClosestPose(currentPosition, onRedAlliance, onLeftSide)));
    }
}
