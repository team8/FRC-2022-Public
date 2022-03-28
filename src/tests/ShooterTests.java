public class ShooterTests {
    public static final ShooterConfig kShooterConfig = Configs.get(ShooterConfig.class);

    @Test
    public void testShooter() {
        var operatorInterface = new OperatorInterface();
        var commands = new Commands();
        operatorInterface.reset(commands);
        var state = new RobotState();
        var shooter = Shooter.getInstance();
        double testFlywheelVelocity = kShooterConfig.maxVelocity;
        commands.setShooterCustomFlywheelVelocity(testFlywheelVelocity, HoodState.LOW);
        state.shooterFlywheelVelocity = testFlywheelVelocity;
        shooter.update(commands, state);
        commands.setShooterCustomFlywheelVelocity(testFlywheelVelocity, HoodState.MEDIUM);
        state.shooterFlywheelVelocity = testFlywheelVelocity;
        shooter.update(commands, state);
        commands.setShooterCustomFlywheelVelocity(testFlywheelVelocity, HoodState.HIGH);
        state.shooterFlywheelVelocity = testFlywheelVelocity;
        shooter.update(commands, state);
        assertFalse(shooter.isReadyToShoot());
}

