package com.palyrobotics.frc2022.behavior.routines.drive;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.trajectory.TrajectoryUtil;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;

public class DrivePathPremadeRoutine extends DrivePathRoutine {

//	private static final String kTrajFolderName = "output";
	private static final Path kConfigFolder = Paths.get(Filesystem.getDeployDirectory().toString());

	private static Path resolveConfigPath(String name) {
		return kConfigFolder.resolve(name);
	}

	private String mTrajectoryFileName;

	public DrivePathPremadeRoutine(String trajectoryFileName) throws JsonProcessingException {
		this.mTrajectoryFileName = trajectoryFileName;
		mTimeout = 10.0;
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		// Required to start the timeout timer
		super.start(commands, state);
//		generateTrajectory();
	}

	@Override
	public void generateTrajectory(Pose2d startingPos) {
		var resolvedConfigPath = resolveConfigPath(this.mTrajectoryFileName);
		try {
			super.mTrajectory = TrajectoryUtil.fromPathweaverJson(resolvedConfigPath);
		} catch (IOException ex) {
			DriverStation.reportError("Unable to open trajectory" + resolveConfigPath(this.mTrajectoryFileName).toString(), ex.getStackTrace());
		}
		mTimeout = mTrajectory.getTotalTimeSeconds() * kTimeoutMultiplier;
	}
}
