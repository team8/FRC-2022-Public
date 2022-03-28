package com.palyrobotics.frc2022.subsystems;

import com.palyrobotics.frc2022.config.constants.IntakeConstants;
import com.palyrobotics.frc2022.config.subsystem.IntakeConfig;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.HardwareAdapter;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.util.config.Configs;
import com.palyrobotics.frc2022.util.control.ControllerOutput;
import com.revrobotics.CANSparkMax;

import edu.wpi.first.wpilibj.Timer;

public class Intake extends SubsystemBase {

	/*
	* Intake states
	* OFF sets intake speed to 0
	* INTAKE sets intake speed to the wanted percent output
	*/
	public enum State {
		OFF, INTAKE, REVERSE
	}

	/*
	* MANUAL: manual control
	* SET_ANGLE: goes to a custom angle
	* IDLE: do nothing
	*/
	public enum DeployState {
		MANUAL, SET_ANGLE, STOWED, DEPLOYED, RE_ZERO, IDLE
	}

	private static Intake sInstance = new Intake();
	private ControllerOutput mOutput = new ControllerOutput();
	private ControllerOutput mDeployOutput = new ControllerOutput();
	private HardwareAdapter.IntakeHardware hardware = HardwareAdapter.IntakeHardware.getInstance();
	private IntakeConfig mConfig = Configs.get(IntakeConfig.class);
	DeployState oldIntakeState = DeployState.IDLE;
	Timer intakeRunningTimer = new Timer();

	private Intake() {
	}

	/* Returns an Instance of Intake */
	public static Intake getInstance() {
		return sInstance;
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		switch (commands.intakeWantedState) {
			/* Intake is retracted and off */
			case OFF:
				mOutput.setIdle();
				break;
			/* Intake is off */
			case INTAKE:
				mOutput.setPercentOutput(mConfig.intakePercentOutput);
				break;
			case REVERSE:
				mOutput.setPercentOutput(-mConfig.intakePercentOutput);
		}

		//System.out.println(state.intakeCurrentPositionDegrees + " " + state.intakeCurrentPotentiometerPosition);
//		if (hardware.intakeDeploySpark.getMotorTemperature() >= 30) {
//			System.out.println("INTAKE SPARK IS: " + hardware.intakeDeploySpark.getMotorTemperature());
//		}
		if (commands.intakeDeployWantedState == DeployState.DEPLOYED || commands.intakeDeployWantedState == DeployState.STOWED) {
			if (oldIntakeState != commands.intakeDeployWantedState) {
				intakeRunningTimer.reset();
				intakeRunningTimer.start();
			}
		}
		switch (commands.intakeDeployWantedState) {
			case MANUAL:
				mDeployOutput.setPercentOutput(commands.intakeDeployWantedPO);
				break;
			case SET_ANGLE:
				if (commands.intakeDeployWantedSetpoint > IntakeConstants.intakeStowedPositionDegrees) {
					commands.intakeDeployWantedSetpoint = IntakeConstants.intakeStowedPositionDegrees;
				} else if (commands.intakeDeployWantedSetpoint < IntakeConstants.intakeMinimumRangeOfMotion) {
					commands.intakeDeployWantedSetpoint = IntakeConstants.intakeMinimumRangeOfMotion;
				}
				mDeployOutput.setTargetPositionProfiled(commands.intakeDeployWantedSetpoint, mConfig.deployGains);
			case STOWED:
				if (Math.abs(state.intakeCurrentPositionDegrees - IntakeConstants.intakeStowedPositionDegrees) > mConfig.intakeDeployTolerance && commands.intakeJustStowed) {
					mDeployOutput.setTargetPositionProfiled(IntakeConstants.intakeStowedPositionDegrees, mConfig.deployGains);
				} else {
					commands.intakeJustStowed = false;
					mDeployOutput.setPercentOutput(0.02);
				}
				break;
			case DEPLOYED:
				if (Math.abs(state.intakeCurrentPositionDegrees - IntakeConstants.intakeIntakingPositionDegrees) > mConfig.intakeDeployTolerance && commands.intakeJustDeployed) {
					/*if (intakeRunningTimer.get() >= mConfig.timeUntilReset) {
						commands.intakeDeployWantedState = DeployState.IDLE;
						break;
					}*/
					mDeployOutput.setTargetPositionProfiled(IntakeConstants.intakeIntakingPositionDegrees, mConfig.deployGains);
				} else {
					mDeployOutput.setPercentOutput(-0.02);
					commands.intakeJustDeployed = false;
				}
				break;
			case RE_ZERO:
				double potentiometerDiff = state.intakeCurrentPotentiometerPosition - IntakeConstants.intakePotentiometerZero;
				double angle = ((-potentiometerDiff) / IntakeConstants.intakePotentiometerTicksInFullRotation) * 360.0;
				hardware.intakeDeploySpark.getEncoder().setPosition(angle);
				commands.intakeJustStowed = true;
				commands.intakeDeployWantedState = DeployState.STOWED;
				break;
			case IDLE:
				mDeployOutput.setIdle();
				break;
		}
		oldIntakeState = commands.intakeDeployWantedState;
	}

	/* Writes current wanted state to the robot. Runs every 20ms */
	@Override
	public void writeHardware(RobotState state) {
		hardware.intakeSpark.setOutput(mOutput);
		hardware.intakeDeploySpark.setOutput(mDeployOutput);
	}

	/* Configures and declares the Intake hardware. Run only once at the beginning */
	@Override
	public void configureHardware() {
		var intakeSpark = HardwareAdapter.IntakeHardware.getInstance().intakeSpark;
		intakeSpark.restoreFactoryDefaults();
		intakeSpark.enableVoltageCompensation(mConfig.kVoltageCompensation);
		intakeSpark.setOpenLoopRampRate(0.0825);
		intakeSpark.setClosedLoopRampRate(0.0825);
		intakeSpark.setInverted(false);
		double maxOutput = 0.75;
		intakeSpark.setOutputRange(-maxOutput, maxOutput);
		intakeSpark.setSmartCurrentLimit((int) Math.round(40.0 / maxOutput));
		intakeSpark.setSecondaryCurrentLimit(70.0 / maxOutput, 10);

		var intakeDeploySpark = HardwareAdapter.IntakeHardware.getInstance().intakeDeploySpark;
		intakeDeploySpark.restoreFactoryDefaults();
		intakeDeploySpark.setIdleMode(CANSparkMax.IdleMode.kBrake);
		intakeDeploySpark.setInverted(true);
		//intakeDeploySpark.setSecondaryCurrentLimit(50);
		intakeDeploySpark.enableVoltageCompensation(mConfig.kVoltageCompensation);
		intakeDeploySpark.getEncoder().setVelocityConversionFactor(IntakeConstants.intakeVelocityConversionFactor);
		intakeDeploySpark.getEncoder().setPositionConversionFactor(IntakeConstants.intakePositionConversionFactor);
	}
}
