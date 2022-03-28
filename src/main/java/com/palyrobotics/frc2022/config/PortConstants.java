package com.palyrobotics.frc2022.config;

import com.palyrobotics.frc2022.util.config.ConfigBase;

@SuppressWarnings ("squid:ClassVariableVisibilityCheck")
public class PortConstants extends ConfigBase {

	/**
	 * Drivetrain
	 */
	public int alvaldiDriveLeftMasterId, alvaldiDriveLeftSlaveId;
	public int alvaldiDriveRightMasterId, alvaldiDriveRightSlaveId;
	public int alvaldiDriveGyroId;

	/**
	 * Climber
	 */
	public int alvaldiClimberTelescopeArmMasterId, alvaldiClimberTelescopeArmSlaveId;
	public int alvaldiSwingerArmMasterId, alvaldiSwingerArmSlaveId;

	/**
	 * Lighting
	 */
	public int alvaldiLightingPwmPort;

	/**
	 * Indexer
	 */
	public int alvaldiIndexerTopId, alvaldiIndexerBottomId;

	/**
	 * Shooter
	 */
	public int alvaldiShooterLowerId, alvaldiShooterUpperId, alvaldiShooterHoodId;

	/**
	 * Intake
	 */
	public int alvaldiIntakeId, alvaldiIntakeLowerId, alvaldiIntakePotentiometerId;

	/**
	 * Turret
	 */
	public int alvaldiTurretMotorId, alvaldiTurretPotentiometerId;
}
