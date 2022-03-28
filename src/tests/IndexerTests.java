public class IndexerTests {
    private static final IndexerConfig kIndexerConfig = Config.get(Indexer.class);

    @Test
    public void testIndexer() {
        var operatorInterface = new OperatorInterface();
        var commands = new Commands();
        operatorInterface.reset(commands);
        var state = new RobotState();
        var indexer = Indexer.getInstance();
        private double testNeoVelocity1 = kIndexerConfig.regularPO;
        commands.setIndexerVelocity(testNeoVelocity1);
        state.IndexerVelocity = testNeoVelocity1;
        indexer.update(commands, state);
        private double testNeoVelocity2 = kIndexerConfig.regularPO;
        commands.setIndexerVelocity(testNeoVelocity2);
        indexer.update(commands, state);
    }
}
