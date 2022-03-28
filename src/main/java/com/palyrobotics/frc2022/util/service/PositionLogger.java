package com.palyrobotics.frc2022.util.service;

import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.util.http.InputThread;

public class PositionLogger {

	private InputThread mThread = new InputThread();
	private RobotState mState;

	public void start(RobotState state) {
		mThread.start("position");
		mState = state;
	}

	public void run(RobotState state) {
		mState = state;
		mThread.addPos(mState.drivePoseMeters.getX(), mState.drivePoseMeters.getY());
	}

}
