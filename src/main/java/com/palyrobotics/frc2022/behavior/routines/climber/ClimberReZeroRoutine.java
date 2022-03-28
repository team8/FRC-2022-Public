package com.palyrobotics.frc2022.behavior.routines.climber;

import java.util.ArrayDeque;

import com.palyrobotics.frc2022.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2022.config.subsystem.ClimberConfig;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.HardwareAdapter;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Climber;
import com.palyrobotics.frc2022.util.config.Configs;

import edu.wpi.first.wpilibj.Timer;

public class ClimberReZeroRoutine extends TimeoutRoutineBase {

	private final ClimberConfig mConfig = Configs.get(ClimberConfig.class);
	private ArrayDeque<Double> prevPosRight = new ArrayDeque<>();
	private ArrayDeque<Double> prevPosLeft = new ArrayDeque<>();
	private Timer resetTimerRight = new Timer();
	private Timer resetTimerLeft = new Timer();
	private boolean startedTimerRight = false;
	private boolean startedTimerLeft = false;

	public ClimberReZeroRoutine() {
		super(20);
	}

	@Override
	public void start(Commands commands, RobotState state) {
		commands.climberArmWantedState = Climber.ArmState.RE_ZERO;
		commands.canRezeroRightArm = true;
		commands.canRezeroLeftArm = true;
	}

	@Override
	protected void update(Commands commands, RobotState state) {
		if (commands.canRezeroLeftArm) {
			if (Math.abs(state.telescopeArmLeftPosition - prevPosLeft.getFirst()) <= mConfig.minDeltaRezero) {
				resetTimerLeft.start();
				commands.canRezeroLeftArm = false;
				startedTimerLeft = true;
			} else {
				prevPosLeft.addLast(state.telescopeArmLeftPosition);
				if (prevPosLeft.size() >= 7) {
					prevPosLeft.removeFirst();
				}
			}
		}
		if (commands.canRezeroRightArm) {
			if (Math.abs(state.telescopeArmRightPosition - prevPosRight.getFirst()) <= mConfig.minDeltaRezero) {
				resetTimerRight.start();
				commands.canRezeroRightArm = false;
				startedTimerRight = true;
			} else {
				prevPosRight.addLast(state.telescopeArmRightPosition);
				if (prevPosRight.size() >= 7) {
					prevPosRight.removeFirst();
				}
			}
		}
	}

	@Override
	public boolean checkIfFinishedEarly(RobotState state) {
		if (startedTimerLeft && startedTimerRight) {
			return resetTimerRight.hasElapsed(0.1) && resetTimerLeft.hasElapsed(0.1);
		}
		return false;
	}

	@Override
	protected void stop(Commands commands, RobotState state) {
		HardwareAdapter.ClimberHardware.getInstance().telescopeArmLeftEncoder.setPosition(0);
		HardwareAdapter.ClimberHardware.getInstance().telescopeArmRightEncoder.setPosition(0);
		commands.climberArmWantedState = Climber.ArmState.IDLE;
	}
}
