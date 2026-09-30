package common.extensions;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.HashMap;
import java.util.Map;

public class TimingExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private final Map<String, Long> startTime = new HashMap<>();
    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        String testName = context.getRequiredTestClass().getPackageName() + "." + context.getRequiredTestMethod();
        startTime.put(testName, System.currentTimeMillis());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        String testName = context.getRequiredTestClass().getPackageName() + "." + context.getRequiredTestMethod();
        long testDuration =  System.currentTimeMillis() - startTime.get(testName);
        System.out.println(
                "Thread - " + Thread.currentThread().getName() + ". Test " + testName + " passed in " + testDuration + " ms");
    }
}
