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
// * bot starts the routine on the mid bar, then moves up to the high bar. extends the telescope arm a
// * certain amount, closes the telescope hook, opens the swinger hook, pulls the telescope hook up,
// * and closes the swinger hook. ends with all 4 hooks on the high bar. also works for high to
// * traversal.
// */
//
//public class ClimberHighBarRoutine extends RoutineBase {
//
//	private RoutineBase mRoutine;
//	private final ClimberConfig mConfig = Configs.get(ClimberConfig.class);
//
//	@Override
//	protected void start(Commands commands, @ReadOnly RobotState state) {
//		mRoutine = new SequentialRoutine(
//				new ClimberOpenTelescopeHookRoutine(),
//				new ClimberPositionControlRoutine(mConfig.telescopeHookDownSlightDistance),
//				new ClimberPositionControlRoutine(mConfig.positionControlDistance),
//				new ClimberCloseTelescopeHookRoutine(),
//				new ClimberOpenSwingerHookRoutine(),
//				new ClimberPositionControlRoutine(mConfig.telescopeSwingerHeightDifference),
//				new ClimberCloseSwingerHookRoutine());
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
