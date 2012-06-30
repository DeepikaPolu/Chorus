package handlerpriority.nodefaulthandler;

import org.chorusbdd.chorus.selftest.AbstractInterpreterTest;
import org.chorusbdd.chorus.selftest.ChorusSelfTestResults;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: nick
 * Date: 25/06/12
 * Time: 22:14
 */
public class TestHandlerPriorityNoDefaultHandler extends AbstractInterpreterTest {

    final String featurePath = "src/test/features/handlerpriority/nodefaulthandler/handlerpriority_nodefault.feature";

    final int expectedExitCode = 0;  //pass

    final String standardOutput = readToString(TestHandlerPriorityNoDefaultHandler.class, "stdout.txt");
    final String standardError = readToString(TestHandlerPriorityNoDefaultHandler.class, "stderr.txt");

    @Test
    public void runTest() throws Exception {

        ChorusSelfTestResults testResults = runFeature(featurePath);

        ChorusSelfTestResults expectedResults = new ChorusSelfTestResults(
            standardOutput,
            standardError,
            expectedExitCode
        );

        checkTestResults(testResults, expectedResults);
    }

}