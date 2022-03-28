package com.palyrobotics.frc2022.behavior.routines.superstructure.shooter;

import java.util.Set;

import com.palyrobotics.frc2022.behavior.RoutineBase;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Shooter;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;

// for autos
public class GetShooterReadyRoutine extends RoutineBase {

	private double shooterWantedAngle;
	private double shooterWantedVelocityLower;
	private double shooterWantedVelocityUpper;

	public GetShooterReadyRoutine(double shooterWantedAngle, double shooterWantedVelocityLower, double shooterWantedVelocityUpper) {
		this.shooterWantedAngle = shooterWantedAngle;
		this.shooterWantedVelocityLower = shooterWantedVelocityLower;
		this.shooterWantedVelocityUpper = shooterWantedVelocityUpper;
	}

	@Override
	protected void update(Commands commands, RobotState state) {
		commands.setShooterCustom(shooterWantedAngle, shooterWantedVelocityLower, shooterWantedVelocityUpper);
	}

	@Override
	protected void stop(Commands commands, RobotState state) {
		super.stop(commands, state);
		commands.setShooterIdle();
	}

	@Override
	public boolean checkFinished(RobotState state) {
		return false;
	}

	@Override
	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return Set.of(Shooter.class);
	}
}
