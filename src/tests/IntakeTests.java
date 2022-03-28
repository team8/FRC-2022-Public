import static org.junit.Assert.*;

import edu.wpi.first.hal.HAL;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.simulation.DoubleSolenoidSim;
import edu.wpi.first.wpilibj.simulation.PWMSim;
import frc.robot.Constants.IntakeConstants;
import org.junit.*;

public class IntakeTests {
    public static final IntakeConfig kIntakeConfig = Config.get(Intake.class);

    @Test
    public void testIntake() {
        var operatorInterface = new OperatorInterface();
        var commands = new Commands();
        operatorInterface.reset(commands);
        var state = new RobotState();
        var intake = Intake.getInstance();
        private double testNeoVelocity1 = kIntakeConfig.regularPO;
        commands.setIntakeVelocity(testNeoVelocity1);
        state.IntakeVelocity = testNeoVelocity1;
        Intake.update(commands, state);
}