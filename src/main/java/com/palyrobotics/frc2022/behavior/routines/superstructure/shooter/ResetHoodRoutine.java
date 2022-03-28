package com.palyrobotics.frc2022.behavior.routines.superstructure.shooter;

import java.util.ArrayDeque;

import com.palyrobotics.frc2022.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2022.config.subsystem.ShooterConfig;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.HardwareAdapter;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Lighting;
import com.palyrobotics.frc2022.subsystems.Shooter;
import com.palyrobotics.frc2022.util.config.Configs;

import edu.wpi.first.wpilibj.Timer;

public class ResetHoodRoutine extends TimeoutRoutineBase {

	private final ShooterConfig config = Configs.get(ShooterConfig.class);
	private ArrayDeque<Double> prevAngle = new ArrayDeque<>();
	private Timer resetTimer = new Timer();
	private boolean startedTimer = false;

	/**
	 * Routine that waits the specified amount of time. Does not require any subsystems.
	 */
	public ResetHoodRoutine() {
		super(3);
		prevAngle.add(10.0);
	}

	@Override
	protected void update(Commands commands, RobotState state) {
		if (!startedTimer) {
			commands.shooterWantedState = Shooter.State.HOOD_PO;
		} else {
			commands.setShooterIdle();
		}

		commands.lightingWantedState = Lighting.State.HOOD_RESET;

	}

	@Override
	public boolean checkIfFinishedEarly(RobotState state) {
		if (!startedTimer) {
			if (Math.abs(state.shooterHoodPosition - prevAngle.getFirst()) <= config.minimumHoodResetDelta) {
				resetTimer.start();
				startedTimer = true;
				return false;
			}
			prevAngle.addLast(state.shooterHoodPosition);
			if (prevAngle.size() >= 7) {
				prevAngle.removeFirst();
			}
			return false;
		} else {
			return resetTimer.hasElapsed(0.1);
		}
	}

	@Override
	protected void stop(Commands commands, RobotState state) {
//		System.out.println("HOOD RESET: " + HardwareAdapter.ShooterHardware.getInstance().sparkEncoder.getPosition());
		HardwareAdapter.ShooterHardware.getInstance().sparkEncoder.setPosition(0);
		commands.setShooterIdle();
		commands.lightingWantedState = Lighting.State.IDLE;
	}
}
