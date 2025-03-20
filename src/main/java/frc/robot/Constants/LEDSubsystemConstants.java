// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.Constants;

import static edu.wpi.first.units.Units.Seconds;

import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.util.Color;

/** Add your docs here. */
public class LEDSubsystemConstants {
    public static final int LED_PORT = 4;
    public static final int BUFFER_LENGTH = 24;
    public static final double BLINK_ON_TIME = 0.1;

    public static final Color SCORING_SIDE_COLOR = Color.kGreen;
    public static final Color NON_SCORING_SIDE_COLOR = Color.kRed;

    public static final LEDPattern OPERATOR_FINAL_FAILSAFE_ON = LEDPattern.solid(Color.kBlue).blink(Seconds.of(.05));
    public static final LEDPattern MANUAL_MODE_ON = LEDPattern.solid(Color.kYellow).blink(Seconds.of(.5));
    public static final LEDPattern CLIMBING_MODE_ON = LEDPattern.solid(Color.kWhite).blink(Seconds.of(2));
}
