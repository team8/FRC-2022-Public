package com.palyrobotics.frc2022.behavior.routines.drive;

import java.util.Set;

import com.palyrobotics.frc2022.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Drive;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;
import com.palyrobotics.frc2022.util.control.DriveOutputs;

public class DriveTimeRoutine extends TimedRoutine {

	private final DriveOutputs mOutput;

	public DriveTimeRoutine(double durationSeconds, DriveOutputs output) {
		super(durationSeconds);
		mOutput = output;
	}

	@Override
	protected void update(Commands commands, RobotState state) {
		commands.setDriveOutputs(mOutput);
	}

	@Override
	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return Set.of(Drive.class);
	}
}
