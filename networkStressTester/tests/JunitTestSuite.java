package networkStressTester.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
	RequestThreadTest.class,
	NetworStresserTest.class
})
public class JunitTestSuite {

}
