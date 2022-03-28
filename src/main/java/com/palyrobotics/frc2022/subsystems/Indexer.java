package com.palyrobotics.frc2022.subsystems;

import com.palyrobotics.frc2022.config.subsystem.IndexerConfig;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.HardwareAdapter;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.util.config.Configs;
import com.palyrobotics.frc2022.util.control.ControllerOutput;
import com.revrobotics.CANSparkMaxLowLevel;

public class Indexer extends SubsystemBase {

	private static Indexer sInstance = new Indexer();
	private ControllerOutput mTopOutput = new ControllerOutput();
	private ControllerOutput mBottomOutput = new ControllerOutput();
	private HardwareAdapter.IndexerHardware mHardware = HardwareAdapter.IndexerHardware.getInstance();
	private IndexerConfig mConfig = Configs.get(IndexerConfig.class);

	private Indexer() {
	}

	public static Indexer getInstance() {
		return sInstance;
	}

	/*
	* IDLE: No indexer movement
	* FEED: When balls are fed into the indexer
	* REVERSE: Reversing the output of the balls
	* CUSTOM_VELOCITY: Manual velocity mode for the indexer
	* WAIT_FOR_INPUT: Waiting for the ball to be fed INDEX: Indexes but not all the way to the shooter
	*/
	public enum State {
		IDLE, INDEX, REVERSE, CUSTOM_VELOCITY, MANUAL, INDEX_FLUSH
	}

	@Override
	public void update(Commands commands, RobotState state) {
		updateController(mBottomOutput, commands.indexerBottomWantedState, false, commands);
		updateController(mTopOutput, commands.indexerTopWantedState, true, commands);
	}

	private void updateController(ControllerOutput controllerOutput, State wantedState, boolean isTop, Commands commands) {
		switch (wantedState) {
			case IDLE:
				controllerOutput.setIdle();
				break;
			case MANUAL:
				if (isTop) {
					controllerOutput.setPercentOutput(mConfig.maxManualVelocity * commands.indexerWantedTopManualOutput);
					break;
				} else {
					controllerOutput.setPercentOutput(mConfig.maxManualVelocity * commands.indexerWantedBottomManualOutput);
					break;
				}
			case INDEX:
				controllerOutput.setTargetVelocityProfiled(mConfig.regularVelocity, isTop ? mConfig.topIndexerGains : mConfig.bottomIndexerGains);
				break;
			case INDEX_FLUSH:
				controllerOutput.setTargetVelocityProfiled(mConfig.flushVelocity, isTop ? mConfig.topIndexerGains : mConfig.bottomIndexerGains);
				break;
			case REVERSE:
				controllerOutput.setPercentOutput(mConfig.reversePO);
				break;
			case CUSTOM_VELOCITY:
				controllerOutput.setPercentOutput(commands.getIndexerVelocity());
				break;
		}
	}

	@Override
	public void writeHardware(RobotState state) {
		mHardware.topSpark.setOutput(mTopOutput);
		mHardware.bottomSpark.setOutput(mBottomOutput);
	}

	@Override
	public void configureHardware() {
		mHardware.topSpark.enableVoltageCompensation(12);
		mHardware.bottomSpark.enableVoltageCompensation(12);
		mHardware.topSpark.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus2, 60);
		mHardware.topSpark.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 20);
		mHardware.bottomSpark.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus2, 60);
		mHardware.bottomSpark.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 20);

	}
}
