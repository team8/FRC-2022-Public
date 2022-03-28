package com.palyrobotics.frc2022.behavior.routines.drive;

import java.util.*;
import java.util.function.Predicate;

import com.palyrobotics.frc2022.behavior.TimeoutRoutineBase;
import com.palyrobotics.frc2022.config.constants.DriveConstants;
import com.palyrobotics.frc2022.config.subsystem.DriveConfig;
import com.palyrobotics.frc2022.robot.Commands;
import com.palyrobotics.frc2022.robot.ReadOnly;
import com.palyrobotics.frc2022.robot.RobotState;
import com.palyrobotics.frc2022.subsystems.Drive;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;
import com.palyrobotics.frc2022.util.config.Configs;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.math.trajectory.constraint.CentripetalAccelerationConstraint;
import edu.wpi.first.math.trajectory.constraint.TrajectoryConstraint;

public class DrivePathRoutine extends TimeoutRoutineBase {
	//todo: fix all timed routines

	private static final double kDrivePathError = 0.1;

	private static final DriveConfig kConfig = Configs.get(DriveConfig.class);
	protected static final double kTimeoutMultiplier = 1.1;
	private final List<Pose2d> mPoses;
	private List<Translation2d> mWaypoints;
	private double mMaxVelocityMetersPerSecond = kConfig.pathVelocityMetersPerSecond,
			mMaxAccelerationMetersPerSecondSq = kConfig.pathAccelerationMetersPerSecondSquared;
	private double mStartingVelocityMetersPerSecond, mEndingVelocityMetersPerSecond;
	private List<TrajectoryConstraint> mConstraints = new ArrayList<>();
	private boolean mShouldReversePath, mDriveInReverse;
	protected Trajectory mTrajectory;

	/**
	 * @param poses Points to move towards from current pose. No initial pose needs to be supplied.
	 */
	public DrivePathRoutine(Pose2d... poses) {
		this(Arrays.asList(poses));
	}

	/**
	 * @see #DrivePathRoutine(Pose2d...)
	 */
	public DrivePathRoutine(List<Pose2d> poses) {
		mPoses = poses;
		mTimeout = 3;
	}

	public DrivePathRoutine startingVelocity(double velocityMetersPerSecond) {
		mStartingVelocityMetersPerSecond = velocityMetersPerSecond;
		return this;
	}

	public DrivePathRoutine addConstraint(TrajectoryConstraint constraint) {
		mConstraints.add(constraint);
		return this;
	}

	public DrivePathRoutine endingVelocity(double velocityMetersPerSecond) {
		mEndingVelocityMetersPerSecond = velocityMetersPerSecond;
		return this;
	}

	public DrivePathRoutine setMovement(double velocityMetersPerSecond, double accelerationMetersPerSecondPerSecond) {
		mMaxVelocityMetersPerSecond = velocityMetersPerSecond;
		mMaxAccelerationMetersPerSecondSq = accelerationMetersPerSecondPerSecond;
		return this;
	}

	public DrivePathRoutine limitWhen(double maxVelocityMetersPerSecond, Predicate<Pose2d> predicate) {
		mConstraints.add(new TrajectoryConstraint() {

			@Override
			public double getMaxVelocityMetersPerSecond(Pose2d poseMeters, double curvatureRadPerMeter, double velocityMetersPerSecond) {
				if (predicate.test(poseMeters)) {
					return maxVelocityMetersPerSecond;
				}
				return Double.POSITIVE_INFINITY;
			}

			@Override
			public MinMax getMinMaxAccelerationMetersPerSecondSq(Pose2d poseMeters, double curvatureRadPerMeter, double velocityMetersPerSecond) {
				return new MinMax(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
			}
		});
		return this;
	}

	public DrivePathRoutine waypoints(List<Translation2d> waypoints) {
		mWaypoints = waypoints;
		return this;
	}

	/**
	 * Robot will try to drive in reverse while traversing the path. Does not reverse the path itself.
	 */
	public DrivePathRoutine driveInReverse() {
		mDriveInReverse = true;
		return this;
	}

	/**
	 * Reverse points in the path. Does not make the robot drive in reverse.
	 */
	public DrivePathRoutine reversePath() {
		mShouldReversePath = true;
		return this;
	}

	/**
	 * Reverses the path and attempts to drive it backwards. Useful for getting a robot back to its
	 * starting position after running a path.
	 */
	public DrivePathRoutine reverse() {
		driveInReverse();
		reversePath();
		return this;
	}

	public void generateTrajectory(Pose2d startingPose) {
		if (mTrajectory == null) {
			var posesWithStart = new LinkedList<>(mPoses);
			if (mShouldReversePath) {
				Collections.reverse(posesWithStart);
			}
			posesWithStart.addFirst(startingPose);
			var trajectoryConfig = DriveConstants.getTrajectoryConfig(mMaxVelocityMetersPerSecond, mMaxAccelerationMetersPerSecondSq);
			trajectoryConfig.addConstraint(new CentripetalAccelerationConstraint(1.6));
			trajectoryConfig.setReversed(mDriveInReverse);
			trajectoryConfig.setStartVelocity(mStartingVelocityMetersPerSecond);
			trajectoryConfig.setEndVelocity(mEndingVelocityMetersPerSecond);
			trajectoryConfig.addConstraints(mConstraints);
			mTrajectory = mWaypoints == null ?
					TrajectoryGenerator.generateTrajectory(posesWithStart, trajectoryConfig) :
					TrajectoryGenerator.generateTrajectory(posesWithStart.getFirst(), mWaypoints, posesWithStart.getLast(),
							trajectoryConfig);
			mTimeout = mTrajectory.getTotalTimeSeconds() * kTimeoutMultiplier;
		} else {
			throw new IllegalStateException("Trajectory already generated!");
		}
	}

	public Trajectory getTrajectory() {
		return mTrajectory;
	}

	@Override
	public void start(Commands commands, @ReadOnly RobotState state) {
		// Required to start the timeout timer
		super.start(commands, state);
		generateTrajectory(state.drivePoseMeters);
	}

	@Override
	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
		return Set.of(Drive.class);
	}

	@Override
	public void update(@ReadOnly Commands commands, @ReadOnly RobotState state) {
		commands.setDriveFollowPath(mTrajectory);
	}

	@Override
	public boolean checkIfFinishedEarly(@ReadOnly RobotState state) {
//		Pose2d finalPose = mTrajectory.getStates().get(mTrajectory.getStates().size() - 1).poseMeters;
		// TODO: possibly implement to see if we are within a tolerance of the end early
//		return isNull(finalPose) ? false : Math.abs(state.drivePoseMeters.getX() - finalPose.getX()) < kDrivePathError && Math.abs(state.drivePoseMeters.getY() - finalPose.getY()) < kDrivePathError;
		return false;
	}

	public void update(RobotState state, Commands commands) {
		commands.setDriveFollowPath(mTrajectory);
	}
}
