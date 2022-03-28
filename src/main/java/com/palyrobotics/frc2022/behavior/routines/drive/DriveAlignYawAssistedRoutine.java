package com.palyrobotics.frc2022.behavior.routines.drive;

import static com.palyrobotics.frc2022.util.Util.getDifferenceInAngleDegrees;

import java.util.Set;

import com.palyrobotics.frc2022.config.VisionConfig;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Drive;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;
import com.palyrobotics.frc2022.util.config.Configs;
import com.palyrobotics.frc2022.vision.Limelight;

public class DriveAlignYawAssistedRoutine extends DriveYawRoutine {

	private final VisionConfig mVisionConfig = Configs.get(VisionConfig.class);
	private final Limelight mLimelight = Limelight.getInstance();
	private final int mVisionPipeline;

	public DriveAlignYawAssistedRoutine(double yawDegrees, int visionPipeline) {
		super(yawDegrees);
		mVisionPipeline = visionPipeline;
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		double yawErrorDegrees = getDifferenceInAngleDegrees(state.driveYawDegrees, mTargetYawDegrees);
		commands.visionWanted = true;
		if (mLimelight.isTargetFound() && Math.abs(yawErrorDegrees) < mVisionConfig.alignSwitchYawAngleMin) {
			commands.setDriveVisionAlign(mVisionPipeline);
		} else {
			commands.setDriveYaw(mTargetYawDegrees);
		}
	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		commands.visionWanted = false;
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		return mLimelight.isAligned();
	}

	@Override
	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return Set.of(Drive.class);
	}
}
