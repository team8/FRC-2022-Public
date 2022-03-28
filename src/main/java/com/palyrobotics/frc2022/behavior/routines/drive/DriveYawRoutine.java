package com.palyrobotics.frc2022.behavior.routines.drive;

import static com.palyrobotics.frc2022.util.Util.getDifferenceInAngleDegrees;

import java.util.Set;

import com.palyrobotics.frc2022.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2022.config.constants.DriveConstants;
import com.palyrobotics.frc2022.config.subsystem.DriveConfig;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Drive;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;
import com.palyrobotics.frc2022.util.Util;
import com.palyrobotics.frc2022.util.config.Configs;

public class DriveYawRoutine extends TimeoutRoutineBase {

	private static final double kTimeoutMultiplier = 1.1;
	protected double mTargetYawDegrees;
	private DriveConfig mDriveConfig = Configs.get(DriveConfig.class);

	/**
	 * Yaw is relative to absolute odometry rotation, not relative to current rotation.
	 */
	public DriveYawRoutine(double yawDegrees) {
		mTimeout = 5.0;
		mTargetYawDegrees = Util.boundAngleNeg180to180Degrees(yawDegrees);
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		super.start(commands, state);
		mTimeout = DriveConstants.calculateTimeToFinishTurn(state.driveYawDegrees, mTargetYawDegrees) *
				kTimeoutMultiplier;
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		commands.setDriveYaw(mTargetYawDegrees);
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		// TODO: check velocity as well
		double yawErrorDegrees = getDifferenceInAngleDegrees(state.driveYawDegrees, mTargetYawDegrees);
		return Math.abs(yawErrorDegrees) < mDriveConfig.allowableYawErrorDegrees;
	}

	public void update(RobotState state, Commands commands) {
		commands.setDriveYaw(mTargetYawDegrees);
	}

	@Override
	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return Set.of(Drive.class);
	}
}
