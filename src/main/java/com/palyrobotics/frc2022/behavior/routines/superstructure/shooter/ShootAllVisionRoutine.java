package com.palyrobotics.frc2022.behavior.routines.superstructure.shooter;

import java.util.Set;

import com.palyrobotics.frc2022.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2022.config.constants.ShooterConstants;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Indexer;
import com.palyrobotics.frc2022.subsystems.Shooter;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;

import edu.wpi.first.wpilibj.Timer;

public class ShootAllVisionRoutine extends TimeoutRoutineBase {

	private boolean shooting = false;
	private Timer readyToShootTimer = new Timer();
	private double kStartTime = 0.9;
	private double kTopIndexOnlyTime = 0.0;

	public ShootAllVisionRoutine() {
		super(22);
	}

	@Override
	public boolean checkIfFinishedEarly(RobotState state) {
		return readyToShootTimer.hasElapsed(2 * ShooterConstants.ballShootTime + kStartTime + kTopIndexOnlyTime);
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		super.start(commands, state);
		commands.setShooterVision();
		readyToShootTimer.start();
	}

	@Override
	protected void update(Commands commands, RobotState state) {
		if (readyToShootTimer.hasElapsed(kStartTime + kTopIndexOnlyTime)) {
			commands.indexerTopWantedState = Indexer.State.INDEX;
			commands.indexerBottomWantedState = Indexer.State.INDEX;
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
