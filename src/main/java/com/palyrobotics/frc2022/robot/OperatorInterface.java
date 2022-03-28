package com.palyrobotics.frc2022.robot;

import static com.palyrobotics.frc2022.util.Util.handleDeadBand;
import static com.palyrobotics.frc2022.vision.Limelight.kOneTimesZoomPipelineId;

import com.palyrobotics.frc2022.behavior.routines.climber.*;
import com.palyrobotics.frc2022.behavior.routines.superstructure.shooter.ResetHoodRoutine;
import com.palyrobotics.frc2022.behavior.routines.superstructure.turret.TurretReZeroRoutine;
import com.palyrobotics.frc2022.config.constants.IntakeConstants;
import com.palyrobotics.frc2022.config.subsystem.ClimberConfig;
import com.palyrobotics.frc2022.config.subsystem.IndexerConfig;
import com.palyrobotics.frc2022.config.subsystem.IntakeConfig;
import com.palyrobotics.frc2022.config.subsystem.ShooterConfig;
import com.palyrobotics.frc2022.robot.HardwareAdapter.Joysticks;
import com.palyrobotics.frc2022.subsystems.*;
import com.palyrobotics.frc2022.util.config.Configs;
import com.palyrobotics.frc2022.util.input.Joystick;
import com.palyrobotics.frc2022.util.input.XboxController;
import com.palyrobotics.frc2022.vision.Limelight;

/**
 * Used to produce {@link Commands}'s from human input. Should only be used in robot package.
 */
public class OperatorInterface {

	public static final double kDeadBand = 0.05;
	public static final int kOnesTimesZoomAlignButton = 4;
	public static final int kOneTimesZoomAlignAlternateButton = 2;
	public static final int kIntakeWantedButton = 3;
	public static final int kIntakeOutButton = 5;
	public static final int kIndexerSpinUpButton = 6;
	public static final int kShooterSpinUpButton = 11;

	private final Joystick mDriveStick = Joysticks.getInstance().driveStick,
			mTurnStick = Joysticks.getInstance().turnStick;
	private Limelight mLimelight = Limelight.getInstance();
	private final IntakeConfig mIntakeConfig = Configs.get(IntakeConfig.class);
	private final ShooterConfig mShooterConfig = Configs.get(ShooterConfig.class);
	private final IndexerConfig mIndexerConfig = Configs.get(IndexerConfig.class);
	private final ClimberConfig mClimberConfig = Configs.get(ClimberConfig.class);
	private final XboxController mOperatorXboxController = Joysticks.getInstance().operatorXboxController;
	private boolean telescopeHooksOpen = true;
	private boolean swingerHooksOpen = true;

	/**
	 * Modifies commands based on operator input devices.
	 */
	void updateCommands(Commands commands, @ReadOnly RobotState state) {

		commands.shouldClearCurrentRoutines = mDriveStick.getTriggerPressed();

		updateDriveCommands(commands);
		updateClimberCommands(commands);
		updateIntakeCommands(commands);
		updateSuperstructure(commands, state);
		updateLightingCommands(commands, state);
		updateTurretCommands(commands);
		mOperatorXboxController.updateLastInputs();
	}

	private void updateDriveCommands(Commands commands) {
		commands.setDriveSlowTurnLeft(mTurnStick.getPOV(0) == 270);
		commands.setDriveSlowMoveBackwards(mDriveStick.getPOV(0) == 180);

		commands.setDriveTeleop(
				handleDeadBand(-mDriveStick.getY(), kDeadBand), handleDeadBand(mTurnStick.getX(), kDeadBand),
				mTurnStick.getTrigger(), mTurnStick.getPOV(0) == 90 || mTurnStick.getPOV(0) == 270, mDriveStick.getPOV(0) == 0 || mDriveStick.getPOV(0) == 180,
				mDriveStick.getTrigger());
		boolean wantsOneTimesAlign = mTurnStick.getRawButton(kOneTimesZoomAlignAlternateButton);
		// Vision align overwrites wanted drive state, using teleop commands when no target is in sight
		if (wantsOneTimesAlign) {
			commands.setDriveVisionAlign(kOneTimesZoomPipelineId);
		}
		if (commands.getDriveWantedState() != Drive.State.VISION_ALIGN && commands.shooterWantedState != Shooter.State.VISION && commands.turretWantedState != Turret.TurretState.TARGETING_VISION) {
			commands.visionWanted = false;
		}
		/* Path Following */
//		if (mOperatorXboxController.getBButtonPressed()) {
//			commands.addWantedRoutine(new SequentialRoutine(
//					new DriveSetOdometryRoutine(0.0, 0.0, 0.0),
//					new DrivePathRoutine(newWaypoint(30.0, 0.0, 0.0))));
//			commands.addWantedRoutine(new SequentialRoutine(
//					new DriveSetOdometryRoutine(0.0, 0.0, 180.0),
//					new DriveYawRoutine(0.0)));
//			commands.addWantedRoutine(new DrivePathRoutine(newWaypoint(0.0, 0.0, 180.0)));
//			commands.addWantedRoutine(new SequentialRoutine(
//					new DriveSetOdometryRoutine(0.0, 0.0, 0.0),
//					new DrivePathRoutine(newWaypoint(40.0, 0.0, 0.0))
//							.setMovement(1.0, 1.0)
//							.endingVelocity(0.5),
//					new DrivePathRoutine(newWaypoint(80.0, 0.0, 0.0))
//							.setMovement(0.5, 1.0)
//							.startingVelocity(0.5)));
//		}
	}

	private void updateTurretCommands(Commands commands) {
		//commands.turretWantedState = Turret.TurretState.MANUAL_TURNING;
		if (mTurnStick.getRawButtonPressed(4)) {
			if (commands.turretWantedState == Turret.TurretState.TARGETING_VISION) {
				commands.setTurretCustomAngle(0.0);
			} else {
				commands.turretWantedState = Turret.TurretState.TARGETING_VISION;
				commands.visionWanted = true;
			}
		}
		if (mOperatorXboxController.getXButtonPressed()) {
			commands.addWantedRoutine(new TurretReZeroRoutine());
		}

		if (commands.turretWantedState == Turret.TurretState.MANUAL_TURNING) {
			commands.turretManualWantedVelocity = handleDeadBand(mOperatorXboxController.getRightX(), kDeadBand * 2);
		}
	}

	private void updateClimberCommands(Commands commands) {
		/*
		Testing climber controls:
		xbox right joystick up / down: move arm
		xbox right joystick left / right: move swinger
		*/
		commands.climberLeftArmWantedPercentOutput = -mOperatorXboxController.getRightY();
		commands.climberSwingerWantedPercentOutput = mOperatorXboxController.getRightX();

		if (handleDeadBand(commands.climberLeftArmWantedPercentOutput, kDeadBand * 2) != 0) {
			commands.climberArmWantedState = Climber.ArmState.MANUAL;
		} else {
			commands.climberArmWantedState = Climber.ArmState.IDLE;
		}

		if (mOperatorXboxController.getDPadLeft()) {
			commands.climberSwingerWantedState = Climber.SwingerState.MANUAL;
			commands.climberSwingerWantedPercentOutput = 0.3;
		} else if (mOperatorXboxController.getDPadRight()) {
			commands.climberSwingerWantedPercentOutput = -0.3;
			commands.climberSwingerWantedState = Climber.SwingerState.MANUAL;
		} else {
			commands.climberSwingerWantedState = Climber.SwingerState.IDLE;
		}
//		if (mOperatorXboxController.getDPadUpPressed()) {
//			commands.addWantedRoutine(new SequentialRoutine(
//					new ClimberOpenTelescopeHookRoutine(),
//					new ClimberPositionControlRoutine(mClimberConfig.positionControlDistance),
//					new ClimberCloseTelescopeHookRoutine(),
//					new ClimberOpenSwingerHookRoutine(),
//					new ClimberPositionControlRoutine(mClimberConfig.telescopeSwingerHeightDifference),
//					new ClimberCloseSwingerHookRoutine()));
//
//		}
	}

	private void updateLightingCommands(Commands commands, @ReadOnly RobotState state) {
		if (commands.lightingWantedState != Lighting.State.HOOD_RESET) {
			commands.lightingWantedState = Lighting.State.OFF;
		}
		if (mLimelight.isAligned()) {
			commands.lightingWantedState = Lighting.State.ALIGNED;
		}
	}

	private void updateSuperstructure(Commands commands, @ReadOnly RobotState state) {
		commands.setIndexerIdle();
		boolean intakeWanted = mTurnStick.getRawButton(kIntakeWantedButton) || mOperatorXboxController.getYButton();
		boolean intakeOutWanted = mOperatorXboxController.getLeftStickButton();

		/*if ((mTurnStick.getRawButtonReleased(kIntakeOutButton) || mOperatorXboxController.getBButtonPressed()) && commands.intakeDeployWantedState != Intake.DeployState.RE_ZERO) {
			if (commands.intakeDeployWantedState == Intake.DeployState.STOWED) {
				commands.intakeDeployWantedState = Intake.DeployState.DEPLOYED;
				commands.intakeJustDeployed = true;
			} else {
				commands.intakeDeployWantedState = Intake.DeployState.STOWED;
				commands.intakeJustStowed = true;
			}
		}
		if (intakeWanted) {
			commands.intakeWantedState = Intake.State.INTAKE;
			commands.indexerBottomWantedState = Indexer.State.INDEX;
		} else if (intakeOutWanted) {
			commands.intakeWantedState = Intake.State.REVERSE;
			commands.indexerBottomWantedState = Indexer.State.REVERSE;
		}*/
		if (mTurnStick.getRawButtonPressed(kIntakeWantedButton) || mOperatorXboxController.getYButtonPressed() || mTurnStick.getRawButtonPressed(kIntakeOutButton) || mOperatorXboxController.getLeftStickButtonPressed()) {
			commands.intakeJustDeployed = true;
		} else if (mTurnStick.getRawButtonReleased(kIntakeWantedButton) || mOperatorXboxController.getYButtonReleased() || mTurnStick.getRawButtonReleased(kIntakeOutButton) || mOperatorXboxController.getLeftStickButtonReleased()) {
			commands.intakeJustStowed = true;
		}

		//commands.intakeDeployWantedState = Intake.DeployState.IDLE;
		if (commands.intakeDeployWantedState != Intake.DeployState.RE_ZERO) {
			if (intakeWanted) {
				commands.intakeDeployWantedState = Intake.DeployState.DEPLOYED;
				commands.intakeWantedState = Intake.State.INTAKE;
				commands.indexerBottomWantedState = Indexer.State.INDEX;
			} else if (intakeOutWanted) {
				commands.intakeDeployWantedState = Intake.DeployState.DEPLOYED;
				commands.intakeWantedState = Intake.State.REVERSE;
				commands.indexerBottomWantedState = Indexer.State.REVERSE;
				commands.indexerTopWantedState = Indexer.State.REVERSE;
			} else {
				commands.intakeDeployWantedState = Intake.DeployState.STOWED;
				commands.intakeDeployWantedSetpoint = IntakeConstants.intakeStowedPositionDegrees;
			}
		}
		if (mOperatorXboxController.getDPadDownPressed() || mTurnStick.getRawButtonPressed(9)) {
			double potentiometerDiff = state.intakeCurrentPotentiometerPosition - IntakeConstants.intakePotentiometerZero;
			double angle = ((-potentiometerDiff) / IntakeConstants.intakePotentiometerTicksInFullRotation) * 360.0;
			HardwareAdapter.IntakeHardware.getInstance().intakeDeploySpark.getEncoder().setPosition(angle);
		}
		double leftThumbYInput = -handleDeadBand(mOperatorXboxController.getLeftY(), kDeadBand);
		if (Math.abs(leftThumbYInput) > 0.1 && (commands.indexerBottomWantedState == Indexer.State.IDLE || commands.indexerBottomWantedState == Indexer.State.MANUAL) && (commands.indexerTopWantedState == Indexer.State.IDLE || commands.indexerTopWantedState == Indexer.State.MANUAL)) {
			commands.setIndexerBottomManual(leftThumbYInput);
			commands.setIndexerTopManual(leftThumbYInput);
		}
		if (mOperatorXboxController.getRightTrigger() || mTurnStick.getRawButton(kIndexerSpinUpButton)) {
			commands.indexerBottomWantedState = Indexer.State.INDEX;
			commands.indexerTopWantedState = Indexer.State.INDEX;
		}
		if (!intakeWanted && Math.abs(leftThumbYInput) < 0.1 && !mOperatorXboxController.getRightTrigger() && !mOperatorXboxController.getLeftTrigger() && !mTurnStick.getRawButton(kIndexerSpinUpButton)) {
			commands.indexerBottomWantedState = Indexer.State.IDLE;
		}
		if (!intakeWanted && !intakeOutWanted) {
			commands.intakeWantedState = Intake.State.OFF;
		}
		if (mOperatorXboxController.getDPadUpPressed()) {
			commands.setShooterIdle();
			commands.addWantedRoutine(new ResetHoodRoutine());
		}
		if (mOperatorXboxController.getRightBumperPressed() || mTurnStick.getRawButtonPressed(12)) {
			commands.setShooterVision();
			commands.visionWanted = true;
			commands.visionWantedPipeline = 0;
		}
		if (mOperatorXboxController.getLeftTrigger()) {
			commands.indexerBottomWantedState = Indexer.State.INDEX_FLUSH;
			commands.indexerTopWantedState = Indexer.State.INDEX_FLUSH;
		}
		if (mOperatorXboxController.getLeftBumperPressed()) {
			commands.setTurretCustomAngle(0.0);
			commands.setShooterCustom(mShooterConfig.shooterCustomHoodAngle, mShooterConfig.shooterCustomLowerFlywheelSpeed, mShooterConfig.shooterCustomHighFlywheelSpeed);
		}
		if ((mOperatorXboxController.getAButtonPressed()) || mTurnStick.getRawButtonPressed(11)) {
			commands.setShooterIdle();
		}
	}

	private void updateIntakeCommands(Commands commands) {
		/*if (mOperatorXboxController.getAButton()) {
			commands.intakeWantedState = Intake.State.INTAKE;
		} else {
			commands.intakeWantedState = Intake.State.OFF;
		}*/
	}

	public void resetPeriodic(Commands commands) {
		commands.lightingWantedState = Lighting.State.IDLE;
	}

	public void reset(Commands commands) {
		commands.routinesWanted.clear();
		commands.setDriveNeutral();
		commands.climberArmWantedState = Climber.ArmState.IDLE;
		commands.climberSwingerWantedState = Climber.SwingerState.IDLE;
		commands.intakeWantedState = Intake.State.OFF;
		commands.intakeDeployWantedState = Intake.DeployState.IDLE;
		commands.lightingWantedState = Lighting.State.IDLE;
		commands.indexerTopWantedState = Indexer.State.IDLE;
		commands.indexerBottomWantedState = Indexer.State.IDLE;
		commands.shooterWantedState = Shooter.State.IDLE;
		commands.shooterWantedAngle = 0.0;
		commands.wantedCompression = true;
		commands.wantedRumble = false;
		commands.visionWanted = false;
		commands.turretWantedState = Turret.TurretState.IDLE;
		mOperatorXboxController.clearLastInputs();
	}
}
