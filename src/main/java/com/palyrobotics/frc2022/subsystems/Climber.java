package com.palyrobotics.frc2022.subsystems;

import com.palyrobotics.frc2022.config.subsystem.ClimberConfig;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.HardwareAdapter;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.util.config.Configs;
import com.palyrobotics.frc2022.util.control.ControllerOutput;
import com.palyrobotics.frc2022.util.control.Spark;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel;

public class Climber extends SubsystemBase {

	/*
	* POSITION_CONTROL = telescoping arm goes to a position, specified in inches with commands.climberArmWantedPosition
	* LOCKED = telescoping arm applies a small output after reaching the desired position, to prevent it from moving
	* MANUAL = operator manually moves the telescoping arm
	* IDLE = telescoping arm output is 0
	*/
	public enum ArmState {
		POSITION_CONTROL, LOCKED, MANUAL, IDLE, RE_ZERO
	}

	public enum SwingerState {
		POSITION_CONTROL, LOCKED, MANUAL, IDLE
	}

	private static final Climber sInstance = new Climber();
	private HardwareAdapter.ClimberHardware hardware = HardwareAdapter.ClimberHardware.getInstance();
	private ControllerOutput mTelescopeLeftArmOutput = new ControllerOutput();
	private ControllerOutput mTelescopeRightArmOutput = new ControllerOutput();
	private ControllerOutput mSwingerOutput = new ControllerOutput();

	private static final ClimberConfig mConfig = Configs.get(ClimberConfig.class);

	private Climber() {
	}

	public static Climber getInstance() {
		return sInstance;
	}

	@Override
	public void update(Commands commands, RobotState state) {
		updateTelescopeArm(commands, state);
		updateSwingerArm(commands, state);
	}

	private void updateTelescopeArm(Commands commands, RobotState state) {
		switch (commands.climberArmWantedState) {
			case POSITION_CONTROL:
				mTelescopeLeftArmOutput.setTargetPositionProfiled(commands.climberArmWantedPosition, mConfig.armVelocityGains);
				mTelescopeRightArmOutput.setTargetPositionProfiled(commands.climberArmWantedPosition, mConfig.armVelocityGains);
				break;
			case LOCKED:
				break;
			case MANUAL:
				mTelescopeLeftArmOutput.setPercentOutput(commands.climberLeftArmWantedPercentOutput);
				mTelescopeRightArmOutput.setPercentOutput(commands.climberLeftArmWantedPercentOutput);
				break;
			case RE_ZERO:
				if (commands.canRezeroLeftArm) {
					mTelescopeLeftArmOutput.setPercentOutput(mConfig.rezeroOutput);
				} else {
					mTelescopeLeftArmOutput.setIdle();
				}
				if (commands.canRezeroRightArm) {
					mTelescopeRightArmOutput.setPercentOutput(mConfig.rezeroOutput);
				} else {
					mTelescopeRightArmOutput.setIdle();
				}
				break;
			case IDLE:
				mTelescopeLeftArmOutput.setIdle();
				mTelescopeRightArmOutput.setIdle();
				break;
		}
	}

	private void updateSwingerArm(Commands commands, RobotState state) {
		switch (commands.climberSwingerWantedState) {
			case POSITION_CONTROL:
				mSwingerOutput.setTargetPositionProfiled(commands.swingerArmWantedPosition, mConfig.swingerVelocityGains);
				break;
			case LOCKED:
				break;
			case MANUAL:
				mSwingerOutput.setPercentOutput(commands.climberSwingerWantedPercentOutput);
				break;
			case IDLE:
				mSwingerOutput.setIdle();
				break;
		}
	}

	@Override
	public void writeHardware(RobotState state) {
		hardware.telescopeArmLeftSpark.setOutput(mTelescopeLeftArmOutput);
		hardware.telescopeArmRightSpark.setOutput(mTelescopeRightArmOutput);
		hardware.swingerArmMasterSpark.setOutput(mSwingerOutput);
	}

	@Override
	public void configureHardware() {
		for (Spark spark : hardware.sparks) {
			spark.restoreFactoryDefaults();
		}

		hardware.telescopeArmLeftSpark.setInverted(false);
		hardware.telescopeArmRightSpark.setInverted(true);
		hardware.swingerArmMasterSpark.setInverted(false);
		hardware.swingerArmSlaveSpark.follow(hardware.swingerArmMasterSpark, false);

		hardware.telescopeArmLeftEncoder.setPositionConversionFactor(1.0);
		hardware.telescopeArmLeftEncoder.setVelocityConversionFactor(1.0);
		hardware.telescopeArmLeftEncoder.setPositionConversionFactor(1.0);
		hardware.telescopeArmRightEncoder.setVelocityConversionFactor(1.0);
		hardware.swingerArmMasterEncoder.setPositionConversionFactor(1.0);
		hardware.swingerArmMasterEncoder.setVelocityConversionFactor(1.0);
		hardware.swingerArmSlaveEncoder.setPositionConversionFactor(1.0);
		hardware.swingerArmSlaveEncoder.setVelocityConversionFactor(1.0);

		hardware.telescopeArmLeftSpark.setIdleMode(CANSparkMax.IdleMode.kBrake);
		hardware.telescopeArmRightSpark.setIdleMode(CANSparkMax.IdleMode.kBrake);
		hardware.swingerArmMasterSpark.setIdleMode(CANSparkMax.IdleMode.kBrake);
		hardware.swingerArmSlaveSpark.setIdleMode(CANSparkMax.IdleMode.kBrake);

		hardware.telescopeArmLeftSpark.getEncoder().setPosition(0.0);
		hardware.telescopeArmRightSpark.getEncoder().setPosition(0.0);
		hardware.swingerArmMasterEncoder.setPosition(0.0);
		hardware.swingerArmSlaveEncoder.setPosition(0.0);

		hardware.telescopeArmLeftSpark.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus1, 40);
		hardware.telescopeArmRightSpark.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus1, 40);
		hardware.swingerArmMasterSpark.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus1, 40);
		hardware.swingerArmSlaveSpark.setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus1, 40);

	}
}
