package frc.robot.Constants;

import java.util.Arrays;
import java.util.List;

import com.pathplanner.lib.path.PathConstraints;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class PathingConstants {
    //find actual values
    public static final PathConstraints PATHFINDING_CONSTRAINTS = new PathConstraints(
        3.0, 
        4.0, 
        Math.toRadians(540), 
        Math.toRadians(270)
    );

    public static final List<Pose2d> BLUE_SIDED_SCORING_POSITIONS = Arrays.asList( //gotta mirror em
        new Pose2d(3.826, 5.174, Rotation2d.fromDegrees(-60)),
        new Pose2d(4.108, 5.335, Rotation2d.fromDegrees(-60)),
        new Pose2d(3.163, 4.356, Rotation2d.fromDegrees(0.0)),
        new Pose2d(3.163, 4.031, Rotation2d.fromDegrees(0.0)),
        new Pose2d(3.533, 3.047, Rotation2d.fromDegrees(60)),
        new Pose2d(3.821, 2.879, Rotation2d.fromDegrees(60)),
        new Pose2d(4.858, 2.711, Rotation2d.fromDegrees(120)),
        new Pose2d(5.144, 2.879, Rotation2d.fromDegrees(120)),
        new Pose2d(5.810, 3.689, Rotation2d.fromDegrees(180)),
        new Pose2d(5.810, 4.018, Rotation2d.fromDegrees(180)),
        new Pose2d(5.438, 5.007, Rotation2d.fromDegrees(240)),
        new Pose2d(5.154, 5.167, Rotation2d.fromDegrees(240))
    );

    public static final List<Pose2d> LEFT_BLUE_SIDED_SCORING_POSITIONS = Arrays.asList( //gotta mirror em
        new Pose2d(3.811, 5.161, Rotation2d.fromDegrees(-60)),
        new Pose2d(3.169, 4.010, Rotation2d.fromDegrees(0.0)),
        new Pose2d(3.840, 2.875, Rotation2d.fromDegrees(60)),
        new Pose2d(5.161, 2.890, Rotation2d.fromDegrees(120)),
        new Pose2d(5.808, 4.040, Rotation2d.fromDegrees(180)),
        new Pose2d(5.135, 5.175, Rotation2d.fromDegrees(240))
    );

    public static final List<Pose2d> RIGHT_BLUE_SIDED_SCORING_POSITIONS = Arrays.asList( //gotta mirror em
        new Pose2d(3.525, 5.004, Rotation2d.fromDegrees(-60)),
        new Pose2d(3.167, 3.679, Rotation2d.fromDegrees(0.0)),
        new Pose2d(4.126, 2.711, Rotation2d.fromDegrees(60)),
        new Pose2d(5.446, 3.051, Rotation2d.fromDegrees(120)),
        new Pose2d(5.808, 4.370, Rotation2d.fromDegrees(180)),
        new Pose2d(4.849, 5.341, Rotation2d.fromDegrees(240))
    );

    public static final double FIELD_WIDTH_METERS = 17.56;
    public static final double FIELD_HEIGHT_METERS = 8.05;

    //both are in botspace. decreasing Y moves the bot back, decreasing X moves it right.
    public static final double X_OFFSET = 0.08;
    public static final double Y_OFFSET = 0.05;

    public static final double ALGAE_X_OFFSET = -0.15;
    public static final double ALGAE_Y_OFFSET = -0.3; //-.65

    public static final double L4_X_OFFSET = 0.15;
    public static final double L4_Y_OFFSET = 0.0;

    public static final List<Pose2d> BLUE_SIDED_ALGAE_INTAKE_POSITIONS = Arrays.asList(
        new Pose2d(3.157, 4.028, Rotation2d.fromDegrees(0.0)),
        new Pose2d(3.825, 2.874, Rotation2d.fromDegrees(60)),
        new Pose2d(5.148, 2.870, Rotation2d.fromDegrees(120)),
        new Pose2d(5.815, 4.030, Rotation2d.fromDegrees(180)),
        new Pose2d(5.151, 5.187, Rotation2d.fromDegrees(240)),
        new Pose2d(3.825, 5.181, Rotation2d.fromDegrees(-60))
    );

    public static final double MAXIMUM_PATHING_DISTANCE = 1.5;
}
