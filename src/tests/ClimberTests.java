public class ClimberTests {
    public static final ClimberConfig kIClimberConfig = Config.get(Climber.class);

    @Test
    public void testClimber() {
        var operatorInterface = new OperatorInterface();
        var commands = new Commands();
        operatorInterface.reset(commands);
        var state = new RobotState();
        var climber = Climber.getInstance();
        private double testNeoVelocity1 = kClimberConfig.regularPO;
        commands.setClimberVelocity(testNeoVelocity1);
        state.ClimberVelocity = testNeoVelocity1;
        Climber.update(commands, state);
}
