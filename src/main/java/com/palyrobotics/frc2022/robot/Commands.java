package com.palyrobotics.frc2022.robot;

import java.util.ArrayList;
import java.util.List;

import com.palyrobotics.frc2022.behavior.RoutineBase;
import com.palyrobotics.frc2022.subsystems.*;
import com.palyrobotics.frc2022.util.control.DriveOutputs;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.trajectory.Trajectory;

/**
 * Commands represent what we want the robot to be doing.
 */
@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class Commands {

	/* Routines */
	public List<RoutineBase> routinesWanted = new ArrayList<>();
	public List<Class<? extends RoutineBase>> wantedCancels = new ArrayList<>();
	public boolean shouldClearCurrentRoutines;
	/* Drive */
	/* Drive Commands */
	private Drive.State driveWantedState;
	// Teleop
	private double driveWantedThrottle, driveWantedWheel;
	private boolean driveWantsQuickTurn, driveWantsSlowTurn, driveWantedSlowTurnLeft, driveWantsBrake, driveWantsSlowMove, driveWantedSlowMoveBackwards;
	// Signal
	private DriveOutputs driveWantedSignal;
	// Path Following
	private Trajectory driveWantedTrajectory;
	public Pose2d driveWantedOdometryPose;
	// Turning
	private double driveWantedYawDegrees;
	/* Climber */
	public Climber.ArmState climberArmWantedState;
	public Climber.SwingerState climberSwingerWantedState;
	public double climberLeftArmWantedPercentOutput;
	public double climberRightArmWantedPercentOutput;
	public double climberSwingerWantedPercentOutput;
	public double climberArmWantedPosition;
	public double swingerArmWantedPosition;
	public boolean canRezeroRightArm;
	public boolean canRezeroLeftArm;
	/* Vision */
	public int visionWantedPipeline;
	public boolean visionWanted;
	/* Miscellaneous */
	public boolean wantedCompression;
	/* Lighting */
	public Lighting.State lightingWantedState;
	/* Turret */
	public Turret.TurretState turretWantedState;
	public double turretCustomAngle;
	public double turretManualWantedVelocity;
	public double turretRezeroAngle;
	/* Shooter */
	public Shooter.State shooterWantedState = Shooter.State.IDLE;
	public double shooterWantedAngle;
	public double shooterWantedVelocityLower;
	public double shooterWantedVelocityUpper;
	public double shooterWantedTopPO;
	public double shooterWantedBottomPO;
	public boolean wantedRumble;

	/* Intake */
	public Intake.State intakeWantedState;
	public Intake.DeployState intakeDeployWantedState;
	public double intakeDeployWantedPO;
	public double intakeDeployWantedSetpoint;
	public boolean intakeJustDeployed;
	public boolean intakeJustStowed;
	/* Indexer */
	public Indexer.State indexerTopWantedState = Indexer.State.IDLE;
	public Indexer.State indexerBottomWantedState = Indexer.State.IDLE;
	public double indexerWantedCustomVelocity;
	public double indexerWantedTopManualOutput;
	public double indexerWantedBottomManualOutput;

	public void addWantedRoutines(RoutineBase... wantedRoutines) {
		for (RoutineBase wantedRoutine : wantedRoutines) {
			addWantedRoutine(wantedRoutine);
		}
	}

	public synchronized void addWantedRoutine(RoutineBase wantedRoutine) {
		routinesWanted.add(wantedRoutine);
	}

	public synchronized void stopRoutine(Class<? extends RoutineBase> wantedRoutine) {
		wantedCancels.add(wantedRoutine);
	}

	public synchronized void stopRoutine(Class<? extends RoutineBase>... wantedRoutines) {
		for (var wantedRoutine : wantedRoutines) {
			stopRoutine(wantedRoutine);
		}
	}

	/* Drive */
	public void setDriveOutputs(DriveOutputs outputs) {
		driveWantedState = Drive.State.OUTPUTS;
		driveWantedSignal = outputs;
	}

	public void setDriveFollowPath(Trajectory trajectory) {
		driveWantedState = Drive.State.FOLLOW_PATH;
		driveWantedTrajectory = trajectory;
	}

	public void setDriveVisionAlign(int visionPipeline) {
		driveWantedState = Drive.State.VISION_ALIGN;
		visionWantedPipeline = visionPipeline;
		visionWanted = true;
	}

	public void setDriveTeleop() {
		setDriveTeleop(0.0, 0.0, false, false, false, false);
	}

	public void setDriveTeleop(double throttle, double wheel, boolean wantsQuickTurn, boolean wantsSlowTurn, boolean wantsSlowMove, boolean wantsBrake) {
		driveWantedState = Drive.State.TELEOP;
		driveWantedThrottle = throttle;
		driveWantedWheel = wheel;
		driveWantsQuickTurn = wantsQuickTurn;
		driveWantsSlowTurn = wantsSlowTurn;
		driveWantsSlowMove = wantsSlowMove;
		driveWantsBrake = wantsBrake;
	}

	public void setDriveNeutral() {
		driveWantedState = Drive.State.NEUTRAL;
	}

	public void setDriveYaw(double yawDegrees) {
		driveWantedState = Drive.State.TURN;
		driveWantedYawDegrees = yawDegrees;
	}

	public void setDriveSlowTurnLeft(boolean wantsSlowTurnLeft) {
		driveWantedSlowTurnLeft = wantsSlowTurnLeft;
	}

	public void setDriveSlowMoveBackwards(boolean wantsSlowMoveBack) {
		driveWantedSlowMoveBackwards = wantsSlowMoveBack;
	}

	public Drive.State getDriveWantedState() {
		return driveWantedState;
	}

	public boolean getDriveWantsQuickTurn() {
		return driveWantsQuickTurn;
	}

	public boolean getDriveWantsSlowTurn() {
		return driveWantsSlowTurn;
	}

	public boolean getDriveWantedSlowTurnLeft() {
		return driveWantedSlowTurnLeft;
	}

	public boolean getDriveWantsSlowMove() {
		return driveWantsSlowMove;
	}

	public boolean getDriveWantedSlowMoveBackwards() {
		return driveWantedSlowMoveBackwards;
	}

	public double getDriveWantedThrottle() {
		return driveWantedThrottle;
	}

	public double getDriveWantedWheel() {
		return driveWantedWheel;
	}

	public boolean getDriveWantsBreak() {
		return driveWantsBrake;
	}

	public Trajectory getDriveWantedTrajectory() {
		return driveWantedTrajectory;
	}

	public double getDriveWantedYawDegrees() {
		return driveWantedYawDegrees;
	}

	public DriveOutputs getDriveWantedSignal() {
		return driveWantedSignal;
	}

	/* Indexer */
	public void setIndexerTopWantedState(Indexer.State newState) {
		indexerTopWantedState = newState;
	}

	public double getIndexerVelocity() {
		return indexerWantedCustomVelocity;
	}

	public void setIndexerCustomVelocity(double velocity) {
		indexerTopWantedState = Indexer.State.CUSTOM_VELOCITY;
		indexerBottomWantedState = Indexer.State.CUSTOM_VELOCITY;
		indexerWantedCustomVelocity = velocity;
	}

	public void setIndexerTopManual(double controllerOutput) {
		indexerTopWantedState = Indexer.State.MANUAL;
		indexerWantedTopManualOutput = controllerOutput;
	}

	public void setIndexerBottomManual(double controllerOutput) {
		indexerBottomWantedState = Indexer.State.MANUAL;
		indexerWantedBottomManualOutput = controllerOutput;
	}

	public void setIndexerIdle() {
		indexerTopWantedState = Indexer.State.IDLE;
		indexerBottomWantedState = Indexer.State.IDLE;
		indexerWantedCustomVelocity = 0;
	}

	/* Shooter */

	public void setShooterVision() {
		this.shooterWantedState = Shooter.State.VISION;
		visionWanted = true;
	}

	public void setShooterIdle() {
		this.shooterWantedState = Shooter.State.IDLE;
	}

	public void setShooterCustom(double shooterWantedAngle, double shooterWantedVelocityLower, double shooterWantedVelocityUpper) {
		this.shooterWantedState = Shooter.State.CUSTOM;
		this.shooterWantedAngle = shooterWantedAngle;
		this.shooterWantedVelocityLower = shooterWantedVelocityLower;
		this.shooterWantedVelocityUpper = shooterWantedVelocityUpper;
	}

	public void setShooterCustomPO(double shooterWantedTopPO, double shooterWantedBottomPO) {
		this.shooterWantedState = Shooter.State.TESTING;
		this.shooterWantedBottomPO = shooterWantedBottomPO;
		this.shooterWantedTopPO = shooterWantedTopPO;
	}

	public double getShooterWantedAngle() {
		return shooterWantedAngle;
	}

	public double getShooterWantedVelocityLower() {
		return shooterWantedVelocityLower;
	}

	public double getShooterWantedVelocityUpper() {
		return shooterWantedVelocityUpper;
	}

	public double getShooterWantedTopPO() {
		return shooterWantedTopPO;
	}

	public double getShooterWantedBottomPO() {
		return shooterWantedBottomPO;
	}

	public Shooter.State getShooterWantedState() {
		return shooterWantedState;
	}

	public void setTurretCustomAngle(double wantedAngle) {
		turretWantedState = Turret.TurretState.SET_ANGLE;
		turretCustomAngle = wantedAngle;
	}

	public void setTurretRezero() {
		turretWantedState = Turret.TurretState.RE_ZERO;
	}

	public void setIntakeBalls() {
		intakeDeployWantedState = Intake.DeployState.DEPLOYED;
		intakeWantedState = Intake.State.INTAKE;
	}

	@Override
	public String toString() {
		var log = new StringBuilder();
		log.append("Wanted routines: ");
		for (RoutineBase routine : routinesWanted) {
			log.append(routine).append(" ");
		}
		return log.append("\n").toString();
	}

	public void copyTo(Commands other) {
		other.driveWantedState = driveWantedState;
		other.shouldClearCurrentRoutines = shouldClearCurrentRoutines;
		other.routinesWanted.addAll(routinesWanted);
		other.intakeWantedState = intakeWantedState;
	}
}
