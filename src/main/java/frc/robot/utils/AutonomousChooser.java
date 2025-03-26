// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.utils;


import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;

/** Add your docs here. */
public class AutonomousChooser {
    SendableChooser<Command> chooser = new SendableChooser<Command>();

    String[] validPaths = {
        "2l4Left",
        "2l4Right",
        "3l4Left"
    };
    
    public AutonomousChooser() {
        chooser.setDefaultOption(validPaths[0], PathLoader.loadAuto(validPaths[0]));
        initChooser();
        SmartDashboard.putData("AutoSelector", chooser);
    }

    private void initChooser() {
        for(String pathName: validPaths) {
            Command auto = PathLoader.loadAuto(pathName);
            chooser.addOption(pathName, auto);
        }
    }

    public Command getSelectedAuto() {
        return chooser.getSelected();
    }
}
