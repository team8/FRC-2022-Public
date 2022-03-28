package com.palyrobotics.frc2022.robot;

import java.util.ArrayList;
import java.util.List;

import com.ctre.phoenix.sensors.PigeonIMU;
import com.palyrobotics.frc2022.config.PortConstants;
import com.palyrobotics.frc2022.util.Loggable;
import com.palyrobotics.frc2022.util.config.Configs;
import com.palyrobotics.frc2022.util.control.Falcon;
import com.palyrobotics.frc2022.util.control.Spark;
import com.palyrobotics.frc2022.util.input.Joystick;
import com.palyrobotics.frc2022.util.input.XboxController;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.wpilibj.*;

/**
 * Represents all hardware components of the robot. Singleton class. Should only be used in robot
 * package. Subdivides hardware into subsystems.
 */
public class HardwareAdapter {

	public static List<Loggable> loggables = new ArrayList<>();

	/**
	 * 4 Falcon 500s (controlled by Talon FX), 1 Pigeon IMU Gyro connected via Talon SRX data cable.
	 */
	public static class DriveHardware {

		private static DriveHardware sInstance;

		public final Falcon leftMasterFalcon = new Falcon(sPortConstants.alvaldiDriveLeftMasterId, "Drive Left Master"),
				leftSlaveFalcon = new Falcon(sPortConstants.alvaldiDriveLeftSlaveId, "Drive Left Slave");
		public final Falcon rightMasterFalcon = new Falcon(sPortConstants.alvaldiDriveRightMasterId, "Drive Right Master"),
				rightSlaveFalcon = new Falcon(sPortConstants.alvaldiDriveRightSlaveId, "Drive Right Slave");

		public final List<Falcon> falcons = List.of(leftMasterFalcon, leftSlaveFalcon,
				rightMasterFalcon, rightSlaveFalcon);

		public final PigeonIMU gyro = new PigeonIMU(sPortConstants.alvaldiDriveGyroId);

		private DriveHardware() {
			loggables.addAll(falcons);
		}

		public static DriveHardware getInstance() {
			if (sInstance == null) sInstance = new DriveHardware();
			return sInstance;
		}
	}

	public static class ClimberHardware {

		private static ClimberHardware sInstance;
		public final Spark telescopeArmLeftSpark = new Spark(sPortConstants.alvaldiClimberTelescopeArmMasterId, "Climber Telescope Arm Master");
		public final Spark telescopeArmRightSpark = new Spark(sPortConstants.alvaldiClimberTelescopeArmSlaveId, "Climber Telescope Arm Slave");
		public final Spark swingerArmMasterSpark = new Spark(sPortConstants.alvaldiSwingerArmMasterId, "Climber Swinger Master");
		public final Spark swingerArmSlaveSpark = new Spark(sPortConstants.alvaldiSwingerArmSlaveId, "Climber Swinger Slave");

		public final List<Spark> sparks = List.of(telescopeArmLeftSpark, telescopeArmRightSpark, swingerArmMasterSpark, swingerArmSlaveSpark);

		public final RelativeEncoder telescopeArmLeftEncoder = telescopeArmLeftSpark.getEncoder();
		public final RelativeEncoder telescopeArmRightEncoder = telescopeArmLeftSpark.getEncoder();
		public final RelativeEncoder swingerArmMasterEncoder = swingerArmMasterSpark.getEncoder();
		public final RelativeEncoder swingerArmSlaveEncoder = swingerArmSlaveSpark.getEncoder();

		//	public final ColorSensorV3 colorSensor = new ColorSensorV3(I2C.Port.kOnboard);

		private ClimberHardware() {
			loggables.addAll(sparks);
		}

		public static ClimberHardware getInstance() {
			if (sInstance == null) sInstance = new ClimberHardware();
			return sInstance;
		}

	}

	public static class ShooterHardware {

		private static ShooterHardware sInstance;

		public Falcon lowerFalcon = new Falcon(sPortConstants.alvaldiShooterLowerId, "Lower Falcon");
		public Falcon upperFalcon = new Falcon(sPortConstants.alvaldiShooterUpperId, "Upper Falcon");
		public Spark hoodSpark = new Spark(sPortConstants.alvaldiShooterHoodId, "Hood Spark");
		public RelativeEncoder sparkEncoder = hoodSpark.getEncoder();

		private ShooterHardware() {
			loggables.addAll(List.of(lowerFalcon, upperFalcon, hoodSpark));
		}

		public static ShooterHardware getInstance() {
			if (sInstance == null) sInstance = new ShooterHardware();
			return sInstance;
		}
	}

	public static class TurretHardware {

		private static TurretHardware sInstance;

		public final Spark spark = new Spark(sPortConstants.alvaldiTurretMotorId, "Turret Motor");
		public final AnalogInput turretPotentiometer = new AnalogInput(sPortConstants.alvaldiTurretPotentiometerId);
		public final RelativeEncoder sparkEncoder = spark.getEncoder();

		private TurretHardware() {
			loggables.add(spark);
		}

		public static TurretHardware getInstance() {
			if (sInstance == null) sInstance = new TurretHardware();
			return sInstance;
		}
	}

	/**
	 * 1 WS2812B LED Strip connected to roboRIO via PWM
	 */
	public static class LightingHardware {

		private static LightingHardware sInstance;
		public final AddressableLED ledStrip = new AddressableLED(sPortConstants.alvaldiLightingPwmPort);

		private LightingHardware() {
		}

		public static LightingHardware getInstance() {
			if (sInstance == null) sInstance = new LightingHardware();
			return sInstance;
		}
	}

	/**
	 * 1 Compressor, 1 PDP, 1 Fisheye USB Camera
	 */
	public static class MiscellaneousHardware {

		private static MiscellaneousHardware sInstance;
		public final PowerDistribution pdp = new PowerDistribution();
//		final UsbCamera fisheyeCam = CameraServer.getInstance().startAutomaticCapture();

		private MiscellaneousHardware() {
		}

		public static MiscellaneousHardware getInstance() {
			if (sInstance == null) sInstance = new MiscellaneousHardware();
			return sInstance;
		}
	}

	/**
	 * 1 NEO
	 */
	public static class IntakeHardware {

		private static IntakeHardware sInstance;

		public final Spark intakeSpark = new Spark(sPortConstants.alvaldiIntakeId, "Intake");
		public final Spark intakeDeploySpark = new Spark(sPortConstants.alvaldiIntakeLowerId, "Intake lower");
		public RelativeEncoder intakeDeployEncoder = intakeDeploySpark.getEncoder();
		public final AnalogInput intakePotentiometer = new AnalogInput(sPortConstants.alvaldiIntakePotentiometerId);

		private IntakeHardware() {
			loggables.add(intakeSpark);
			loggables.add(intakeDeploySpark);
		}

		public static IntakeHardware getInstance() {
			if (sInstance == null) sInstance = new IntakeHardware();
			return sInstance;
		}
	}

	/**
	 * 2 Joysticks, 1 Xbox Controller
	 */
	public static class Joysticks {

		private static final Joysticks sInstance = new Joysticks();
		public final Joystick driveStick = new Joystick(0), turnStick = new Joystick(1);
		public final XboxController operatorXboxController = new XboxController(2);

		private Joysticks() {
			loggables.add(driveStick);
			loggables.add(turnStick);
			loggables.add(operatorXboxController);
		}

		public static Joysticks getInstance() {
			return sInstance;
		}
	}

	public static class IndexerHardware {

		private static IndexerHardware sInstance;

		public final Spark topSpark = new Spark(sPortConstants.alvaldiIndexerTopId, "Indexer Top");
		public final Spark bottomSpark = new Spark(sPortConstants.alvaldiIndexerBottomId, "Indexer Bottom");

		private IndexerHardware() {
			loggables.add(topSpark);
			loggables.add(bottomSpark);
		}

		public static IndexerHardware getInstance() {
			if (sInstance == null) sInstance = new IndexerHardware();
			return sInstance;
		}
	}

	private static final PortConstants sPortConstants = Configs.get(PortConstants.class);

	private HardwareAdapter() {
	}

}
