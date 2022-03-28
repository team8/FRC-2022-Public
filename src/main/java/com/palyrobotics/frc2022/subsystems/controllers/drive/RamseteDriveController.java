package com.palyrobotics.frc2022.subsystems.controllers.drive;

import static com.palyrobotics.frc2022.config.constants.DriveConstants.kKinematics;

import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Drive;
import com.palyrobotics.frc2022.util.dashboard.LiveGraph;

import edu.wpi.first.math.controller.RamseteController;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.DifferentialDriveWheelSpeeds;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj.Timer;

public class RamseteDriveController extends Drive.DriveController {

	public static final double kB = 2.0, kZeta = 0.8;

	private final Timer mTimer = new Timer();
	private RamseteController mController;
	private Trajectory mTrajectory;

	public RamseteDriveController() {
		mTimer.start();
	}

	@Override
	public void updateSignal(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		if (state.driveIsGyroReady) {
			Trajectory wantedTrajectory = commands.getDriveWantedTrajectory();
			if (mTrajectory != wantedTrajectory) {
				// We want our update function to define every state associated with
				// this controller, so we must take care to handle these internal states to
				// avoid errors and state bleeding.
				mTrajectory = wantedTrajectory;
				mController = new RamseteController(kB, kZeta);
				mTimer.reset();
			}
			Trajectory.State targetPose = wantedTrajectory.sample(mTimer.get());
			ChassisSpeeds speeds = mController.calculate(state.drivePoseMeters, targetPose);
			DifferentialDriveWheelSpeeds wheelSpeeds = kKinematics.toWheelSpeeds(speeds);
			mOutputs.leftOutput.setTargetVelocity(wheelSpeeds.leftMetersPerSecond, mConfig.velocityGains);
			mOutputs.rightOutput.setTargetVelocity(wheelSpeeds.rightMetersPerSecond, mConfig.velocityGains);
//		mOutputs.leftOutput.setTargetVelocityProfiled(wheelSpeeds.leftMetersPerSecond, mConfig.profiledVelocityGains);
//		mOutputs.rightOutput.setTargetVelocityProfiled(wheelSpeeds.rightMetersPerSecond, mConfig.profiledVelocityGains);
//		LiveGraph.add("targetLeftVelocity", wheelSpeeds.leftMetersPerSecond);
			LiveGraph.add("time", mTimer.get());
			LiveGraph.add("targetRightVelocity", wheelSpeeds.rightMetersPerSecond);
			LiveGraph.add("currentPoseX", state.drivePoseMeters.getTranslation().getX());
			LiveGraph.add("currentPoseY", state.drivePoseMeters.getTranslation().getY());
			LiveGraph.add("currentPoseR", state.drivePoseMeters.getRotation().getDegrees());
			LiveGraph.add("leftVelocity", state.driveLeftVelocity);
			LiveGraph.add("rightVelocity", state.driveRightVelocity);
			LiveGraph.add("targetPoseX", targetPose.poseMeters.getTranslation().getX());
			LiveGraph.add("targetPoseY", targetPose.poseMeters.getTranslation().getY());
			LiveGraph.add("targetPoseR", targetPose.poseMeters.getRotation().getDegrees());
		}
	}
}
