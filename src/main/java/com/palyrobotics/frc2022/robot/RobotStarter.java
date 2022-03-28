package com.palyrobotics.frc2022.robot;

import edu.wpi.first.wpilibj.RobotBase;

public final class RobotStarter {

	private RobotStarter() {
	}

	public static void main(String... args) {
		RobotBase.startRobot(Robot::new);
	}
}
