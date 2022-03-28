package com.palyrobotics.frc2022.behavior.routines.drive;

import java.util.Set;

import com.palyrobotics.frc2022.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2022.config.VisionConfig;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Drive;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;
import com.palyrobotics.frc2022.util.config.Configs;
import com.palyrobotics.frc2022.vision.Limelight;

public class DriveAlignRoutine extends TimeoutRoutineBase {

	private final Limelight mLimelight = Limelight.getInstance();
	private final VisionConfig mVisionConfig = Configs.get(VisionConfig.class);
	private final int mVisionPipeline;

	public DriveAlignRoutine(int visionPipeline) {
		super(3.0);
		mVisionPipeline = visionPipeline;
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		super.start(commands, state);
		commands.visionWanted = true;
		commands.visionWantedPipeline = mVisionPipeline;
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
		return mLimelight.isAligned();
	}

	public void update(RobotState state, Commands commands) {
		commands.setDriveVisionAlign(mVisionPipeline);
	}

	@Override
	protected void update(Commands commands, @ReadOnly RobotState state) {
		commands.setDriveVisionAlign(mVisionPipeline);
	}

	@Override
	protected void stop(Commands commands, @ReadOnly RobotState state) {
		commands.visionWanted = false;
	}

	@Override
	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return Set.of(Drive.class);
	}
}
