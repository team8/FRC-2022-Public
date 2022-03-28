package com.palyrobotics.frc2022.robot;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.esotericsoftware.minlog.Log;
import com.palyrobotics.frc2022.auto.PranavRobbie4BallAuto;
import com.palyrobotics.frc2022.auto.TwoBallAuto;
import com.palyrobotics.frc2022.behavior.*;
import com.palyrobotics.frc2022.behavior.routines.drive.DrivePathRoutine;
import com.palyrobotics.frc2022.behavior.routines.drive.DriveSetOdometryRoutine;
import com.palyrobotics.frc2022.behavior.routines.superstructure.intake.IntakeRezeroRoutine;
import com.palyrobotics.frc2022.behavior.routines.superstructure.shooter.ResetHoodRoutine;
import com.palyrobotics.frc2022.behavior.routines.superstructure.turret.TurretReZeroRoutine;
import com.palyrobotics.frc2022.config.RobotConfig;
import com.palyrobotics.frc2022.subsystems.*;
import com.palyrobotics.frc2022.subsystems.Climber;
import com.palyrobotics.frc2022.subsystems.Drive;
import com.palyrobotics.frc2022.subsystems.Indexer;
import com.palyrobotics.frc2022.subsystems.Intake;
import com.palyrobotics.frc2022.subsystems.Lighting;
import com.palyrobotics.frc2022.subsystems.Shooter;
import com.palyrobotics.frc2022.subsystems.SubsystemBase;
import com.palyrobotics.frc2022.util.LoopOverrunDebugger;
import com.palyrobotics.frc2022.util.Util;
import com.palyrobotics.frc2022.util.commands.CommandReceiverService1;
import com.palyrobotics.frc2022.util.config.ConfigUploadManager;
import com.palyrobotics.frc2022.util.config.Configs;
import com.palyrobotics.frc2022.util.csvlogger.CSVWriter;
import com.palyrobotics.frc2022.util.dashboard.LiveGraph;
import com.palyrobotics.frc2022.util.http.HttpInput;
import com.palyrobotics.frc2022.util.http.LightHttpServer;
import com.palyrobotics.frc2022.util.service.*;
import com.palyrobotics.frc2022.util.service.NetworkLoggerService;
import com.palyrobotics.frc2022.util.service.RobotService;
import com.palyrobotics.frc2022.util.service.TelemetryService;
import com.palyrobotics.frc2022.vision.Limelight;
import com.palyrobotics.frc2022.vision.LimelightControlMode;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import org.json.JSONObject;

public class Robot extends TimedRobot {

	public static final double kPeriod = 0.02;
	private static final String kLoggerTag = Util.classToJsonName(Robot.class);
	private static final boolean kCanUseHardware = RobotBase.isReal() || !System.getProperty("os.name").startsWith("Mac");
	private final RobotState mRobotState = new RobotState();
	private final Limelight mLimelight = Limelight.getInstance();
	private final RobotConfig mConfig = Configs.get(RobotConfig.class);
	private final OperatorInterface mOperatorInterface = new OperatorInterface();
	private final RoutineManager mRoutineManager = new RoutineManager();
	private final HardwareReader mHardwareReader = new HardwareReader();
	private final HardwareWriter mHardwareWriter = new HardwareWriter();
	private final Commands mCommands = new Commands();
	private final LightHttpServer mServer = new LightHttpServer(mCommands, mRobotState, mRoutineManager);

	/* Subsystems */
	private final Drive mDrive = Drive.getInstance();
	private final Shooter mShooter = Shooter.getInstance();
	private final Climber mClimber = Climber.getInstance();
	private final Lighting mLighting = Lighting.getInstance();
	private final Indexer mIndexer = Indexer.getInstance();
	private final Intake mIntake = Intake.getInstance();
	private final Turret mTurret = Turret.getInstance();

	// for cc2 "serverService", "networkLoggerService", "graphingService","telemetryService", "webService"
	private Set<SubsystemBase> mSubsystems = Set.of(mDrive, mLighting, mShooter, mIndexer, mIntake, mTurret, mClimber),
			mEnabledSubsystems;
	private final Set<RobotService> mServices = Set.of(new ServerService(mServer), new NetworkLoggerService(mServer),
			new GraphingService(mServer), new TelemetryService(mServer), new WebService(RobotBase.isReal()),
			new CommandReceiverService1(), new NetworkLoggerService1(), new TelemetryService1(), new LoggingService());
	private Set<RobotService> mEnabledServices;

	private PositionLogger mPositionLogger = new PositionLogger();

	public Robot() {
		super(kPeriod);
	}

	@Override
	public void robotInit() {
		LiveWindow.disableAllTelemetry();

		String setupSummary = setupSubsystemsAndServices();

		mEnabledSubsystems.forEach(SubsystemBase::configureHardware);
		mHardwareWriter.configureMiscellaneousHardware();

		mEnabledServices.forEach(RobotService::start);

		Log.info(kLoggerTag, setupSummary);

		mCommands.lightingWantedState = Lighting.State.INIT;
		if (mEnabledSubsystems.contains(mLighting)) {
			mLighting.update(mCommands, mRobotState);
			mLighting.writeHardware(mRobotState);
		}

		// pre-loads config information to upload manager
		Iterator configIterator = Configs.getActiveConfigNames().iterator();
		JSONObject configJson = new JSONObject();
		Object temp;
		while (configIterator.hasNext()) {
			temp = configIterator.next();
			configJson.put(temp.toString(), new JSONObject(Configs.get(Configs.getClassFromName(temp.toString())).toString()));
		}
		Log.info(configJson.toString());
		HttpInput.getInstance().setConfigInput(configJson);
		HttpInput.getInstance().setConfigInput(configJson);
		ConfigUploadManager.getInstance().updateConfig(configJson);

		mPositionLogger.start(mRobotState);
	}

	@Override
	public void simulationInit() {
//		Log.info(kLoggerTag, "Writing path CSV file...");
		pathToCsv();
	}

	private void pathToCsv() {
//		var drivePath = new StartCenterFriendlyTrenchThreeShootThree().getRoutine();
		RoutineBase drivePath = null;
		try (var writer = new PrintWriter(new BufferedWriter(new FileWriter("auto.csv")))) {
			writer.write("x,y,d" + '\n');
			var points = new LinkedList<Pose2d>();
			recurseRoutine(drivePath, points);
			for (Pose2d pose : points) {
				Translation2d point = pose.getTranslation();
				writer.write(String.format("%f,%f,%f%n", point.getY() * -39.37, point.getX() * 39.37, pose.getRotation().getDegrees()));
			}
		} catch (IOException writeException) {
			writeException.printStackTrace();
		}
	}

	private void recurseRoutine(RoutineBase routine, Deque<Pose2d> points) {
		if (routine instanceof MultipleRoutineBase) {
			var multiple = (MultipleRoutineBase) routine;
			for (RoutineBase childRoutine : multiple.getRoutines()) {
				recurseRoutine(childRoutine, points);
			}
		} else if (routine instanceof DriveSetOdometryRoutine) {
			var odometry = (DriveSetOdometryRoutine) routine;
			var pose = odometry.getTargetPose();
			points.addLast(pose);
		} else if (routine instanceof DrivePathRoutine) {
			var path = (DrivePathRoutine) routine;
			System.out.println(points.getLast());
			path.generateTrajectory(points.getLast());
			for (Trajectory.State state : path.getTrajectory().getStates()) {
				var pose = state.poseMeters;
				points.addLast(pose);
			}
		}
	}

	@Override
	public void disabledInit() {
		mRobotState.gamePeriod = RobotState.GamePeriod.DISABLED;
		resetCommandsAndRoutines();

		//	HardwareAdapter.Joysticks.getInstance().operatorXboxController.setRumble(false);
		updateDriveNeutralMode(mConfig.coastDriveWhenDisabled);

		//CSVWriter.write();

		mCommands.lightingWantedState = Lighting.State.DISABLE;
		if (mEnabledSubsystems.contains(mLighting)) {
			mLighting.update(mCommands, mRobotState);
			mLighting.writeHardware(mRobotState);
		}
	}

	@Override
	public void autonomousInit() {
		startStage(RobotState.GamePeriod.AUTO);
		mCommands.lightingWantedState = Lighting.State.OFF;
		mCommands.addWantedRoutine(new SequentialRoutine(new IntakeRezeroRoutine(0.5), new PranavRobbie4BallAuto().getRoutine()));
	}

	private void startStage(RobotState.GamePeriod period) {
		mRobotState.gamePeriod = period;
		resetCommandsAndRoutines();
		updateDriveNeutralMode(false);
		CSVWriter.cleanFile();
		CSVWriter.resetTimer();
	}

	@Override
	public void teleopInit() {
		//TODO: check ramifications of line above
		startStage(RobotState.GamePeriod.TELEOP);
		mCommands.addWantedRoutine(new ParallelRoutine(new ResetHoodRoutine(), new TurretReZeroRoutine()));
		mCommands.intakeDeployWantedState = Intake.DeployState.RE_ZERO;
		mCommands.setDriveTeleop();
		mCommands.lightingWantedState = Lighting.State.OFF;
		if (mEnabledSubsystems.contains(mLighting)) {
			mLighting.update(mCommands, mRobotState);
			mLighting.writeHardware(mRobotState);
		}
	}

	@Override
	public void testInit() {
		startStage(RobotState.GamePeriod.TESTING);
	}

	@Override
	public void robotPeriodic() {
		for (RobotService robotService : mEnabledServices) {
			robotService.update(mRobotState, mCommands);
		}
		LiveGraph.add("visionEstimatedDistance", mLimelight.getEstimatedDistanceInches());
		LiveGraph.add("isEnabled", isEnabled());
	}

	@Override
	public void simulationPeriodic() {

	}

	@Override
	public void disabledPeriodic() {

		updateVision(mConfig.enableVisionWhenDisabled, mConfig.visionPipelineWhenDisabled);
		updateDriveNeutralMode(mConfig.coastDriveWhenDisabled);
		//System.out.println(HardwareAdapter.DriveHardware.getInstance().gyro.getState());
		if (mEnabledSubsystems.contains(mLighting)) {
			mLighting.update(mCommands, mRobotState);
			mLighting.writeHardware(mRobotState);
		}
	}

	@Override
	public void autonomousPeriodic() {
//		mOperatorInterface.defaults(mCommands);
		updateRobotState();
		mRoutineManager.update(mCommands, mRobotState);
		updateSubsystemsAndApplyOutputs();
		mOperatorInterface.resetPeriodic(mCommands);
	}

	public static LoopOverrunDebugger mDebugger = new LoopOverrunDebugger("teleop", 0.02);

	@Override
	public void teleopPeriodic() {
//		mOperatorInterface.defaults(mCommands);
		mPositionLogger.run(mRobotState);
		mDebugger.reset();
		updateRobotState();
		mDebugger.addPoint("robotState");
		mOperatorInterface.updateCommands(mCommands, mRobotState);
		mDebugger.addPoint("updateCommands");
		mRoutineManager.update(mCommands, mRobotState);
		mDebugger.addPoint("routineManagerUpdate");
		updateSubsystemsAndApplyOutputs();
		mDebugger.addPoint("updateSubsystemsAndApplyOutputs");
		mDebugger.finish();
		mOperatorInterface.resetPeriodic(mCommands);
	}

	@Override
	public void testPeriodic() {
		teleopPeriodic();
	}

	private void resetCommandsAndRoutines() {
		mOperatorInterface.reset(mCommands);
		mRoutineManager.clearRunningRoutines();
		updateSubsystemsAndApplyOutputs();
	}

	private void updateRobotState() {
		mHardwareReader.updateState(mEnabledSubsystems, mRobotState);
	}

	/**
	 * Resets the pose based on {@link Commands#driveWantedOdometryPose}. Sets it to null afterwards to
	 * avoid writing multiple updates to the controllers.
	 */
	private void resetOdometryIfWanted() {
		Pose2d wantedPose = mCommands.driveWantedOdometryPose;
		if (wantedPose != null) {
			mRobotState.resetOdometry(wantedPose);
			mHardwareWriter.resetDriveSensors(wantedPose);
			mCommands.driveWantedOdometryPose = null;
		}
	}

	private void updateSubsystemsAndApplyOutputs() {
		resetOdometryIfWanted();

		for (SubsystemBase subsystem : mEnabledSubsystems) {
			subsystem.update(mCommands, mRobotState);
			subsystem.logSubsystem(mRobotState);
			mDebugger.addPoint(subsystem.getName());
		}
		mEnabledSubsystems.forEach(s -> s.writeHardware(mRobotState));
		mDebugger.addPoint("updateHardware");
		updateVision(mCommands.visionWanted, mCommands.visionWantedPipeline);
	}

	private void updateVision(boolean visionWanted, int visionPipeline) {
		if (visionWanted) {
			mLimelight.setCamMode(LimelightControlMode.CamMode.VISION);
			mLimelight.setLEDMode(LimelightControlMode.LedMode.FORCE_ON);
		} else {
			mLimelight.setCamMode(LimelightControlMode.CamMode.DRIVER);
			mLimelight.setLEDMode(LimelightControlMode.LedMode.FORCE_OFF);
		}
		mLimelight.setPipeline(visionPipeline);
	}

	private String setupSubsystemsAndServices() {
		// TODO: same logic twice in a row
		Map<String, RobotService> configToService = mServices.stream()
				.collect(Collectors.toUnmodifiableMap(RobotService::getConfigName, Function.identity()));
		mEnabledServices = mConfig.enabledServices.stream().map(configToService::get)
				.collect(Collectors.toUnmodifiableSet());
		Map<String, SubsystemBase> configToSubsystem = mSubsystems.stream()
				.collect(Collectors.toUnmodifiableMap(SubsystemBase::getName, Function.identity()));
		mEnabledSubsystems = mConfig.enabledSubsystems.stream().map(configToSubsystem::get)
				.collect(Collectors.toUnmodifiableSet());
		var summaryBuilder = new StringBuilder("\n");
		summaryBuilder.append("===================\n");
		summaryBuilder.append("Enabled subsystems:\n");
		summaryBuilder.append("-------------------\n");
		for (SubsystemBase enabledSubsystem : mEnabledSubsystems) {
			summaryBuilder.append(enabledSubsystem.getName()).append("\n");
		}
		summaryBuilder.append("=================\n");
		summaryBuilder.append("Enabled services:\n");
		summaryBuilder.append("-----------------\n");
		for (RobotService enabledService : mEnabledServices) {
			summaryBuilder.append(enabledService.getConfigName()).append("\n");
		}
		return summaryBuilder.toString();
	}

	private void updateDriveNeutralMode(boolean shouldCoast) {
		if (mEnabledSubsystems.contains(mDrive)) mHardwareWriter.setDriveNeutralMode(shouldCoast ? NeutralMode.Coast : NeutralMode.Brake);
	}
}
