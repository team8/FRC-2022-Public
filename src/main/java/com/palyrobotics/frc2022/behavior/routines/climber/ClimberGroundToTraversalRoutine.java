//package com.palyrobotics.frc2022.behavior.routines.climber;
//
//import java.util.Set;
//
//import com.palyrobotics.frc2022.behavior.RoutineBase;
//import com.palyrobotics.frc2022.behavior.SequentialRoutine;
//import com.palyrobotics.frc2022.config.subsystem.ClimberConfig;
//import com.palyrobotics.frc2022.robot.Commands;
//import com.palyrobotics.frc2022.robot.ReadOnly;
//import com.palyrobotics.frc2022.robot.RobotState;
//import com.palyrobotics.frc2022.subsystems.Climber;
//import com.palyrobotics.frc2022.subsystems.SubsystemBase;
//import com.palyrobotics.frc2022.util.config.Configs;
//
///*
// * bot starts the routine on the ground, with the climber aligned to the mid bar. bot goes all the
// * way up to the traversal bar and hangs there.
// */
//
//public class ClimberGroundToTraversalRoutine extends RoutineBase {
//
//	private RoutineBase mRoutine;
//	private final ClimberConfig mConfig = Configs.get(ClimberConfig.class);
//
//	@Override
//	protected void start(Commands commands, @ReadOnly RobotState state) {
//		mRoutine = new SequentialRoutine(
//				new ClimberMidBarRoutine(),
//				new ClimberHighBarRoutine(),
//				new ClimberHighBarRoutine());
//		commands.addWantedRoutine(mRoutine);
//	}
//
//	@Override
//	public boolean checkFinished(RobotState state) {
//		return mRoutine.isFinished();
//	}
//
//	@Override
//	public Set<Class<? extends SubsystemBase>> getRequiredSubsystems() {
//		return Set.of(Climber.class);
//	}
//}
