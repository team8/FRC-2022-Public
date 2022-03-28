package com.palyrobotics.frc2022.behavior.routines.superstructure.shooter;

import java.util.Set;

import com.palyrobotics.frc2022.behavior.routines.TimedRoutine;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Indexer;
import com.palyrobotics.frc2022.subsystems.Shooter;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;

import edu.wpi.first.wpilibj.Timer;

public class ShootCustomRoutine extends TimedRoutine {

	private Timer readyToShootTimer = new Timer();
	private double shooterWantedAngle;
	private double shooterWantedVelocityLower;
	private double shooterWantedVelocityUpper;

	public ShootCustomRoutine(double timeoutSeconds, double shooterWantedAngle, double shooterWantedVelocityLower, double shooterWantedVelocityUpper) {
		super(timeoutSeconds);
		this.shooterWantedAngle = shooterWantedAngle;
		this.shooterWantedVelocityLower = shooterWantedVelocityLower;
		this.shooterWantedVelocityUpper = shooterWantedVelocityUpper;
	}

	@Override
	protected void update(Commands commands, RobotState state) {
		commands.setShooterCustom(shooterWantedAngle, shooterWantedVelocityLower, shooterWantedVelocityUpper);
		if (state.shooterReadyToShoot && readyToShootTimer.get() <= 0) {
			readyToShootTimer.start();
		}
		if (readyToShootTimer.get() > 0) {
			commands.indexerTopWantedState = Indexer.State.INDEX;
			commands.indexerBottomWantedState = Indexer.State.INDEX;
		} else {
			commands.setIndexerIdle();
		}
	}

	@Override
	protected void stop(Commands commands, RobotState state) {
		super.stop(commands, state);
		commands.setShooterIdle();
		commands.setIndexerIdle();
	}

	@Override
	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return Set.of(Shooter.class, Indexer.class);
	}
}
