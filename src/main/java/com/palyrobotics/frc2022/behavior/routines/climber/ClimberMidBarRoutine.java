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
// * bot starts on the shadow line, extends the T arm upwards, closes the T hooks on the mid bar,
// * retracts the T arm until the S arm reaches the mid bar, closes the S hooks, and opens the T
// * hooks.
// */
//
//public class ClimberMidBarRoutine extends RoutineBase {
//
//	private final ClimberConfig mConfig = Configs.get(ClimberConfig.class);
//	private SequentialRoutine mRoutine = new SequentialRoutine(
//			new ClimberOpenTelescopeHookRoutine(),
//			new ClimberPositionControlRoutine(mConfig.positionControlDistance),
//			new ClimberCloseTelescopeHookRoutine(),
//			new ClimberOpenSwingerHookRoutine(),
//			new ClimberPositionControlRoutine(mConfig.telescopeSwingerHeightDifference),
//			new ClimberCloseSwingerHookRoutine(),
//			new ClimberOpenTelescopeHookRoutine());
//
//	@Override
//	protected void update(Commands commands, RobotState state) {
//		mRoutine.update(commands, state);
//	}
//
//	@Override
//	protected void start(Commands commands, @ReadOnly RobotState state) {
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
