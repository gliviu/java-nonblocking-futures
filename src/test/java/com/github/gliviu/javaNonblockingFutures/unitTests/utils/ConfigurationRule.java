package com.github.gliviu.javaNonblockingFutures.unitTests.utils;

import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import com.github.gliviu.javaNonblockingFutures.Promise;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

/**
 * JUnit rule that applies {@link Configuration} to each test.
 */
public class ConfigurationRule implements MethodRule {
    @Override
    public Statement apply(Statement base, FrameworkMethod method, Object target) {
        BaseUnitTests unitTests = (BaseUnitTests) target;
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {

                Configuration[] configurations = null;
                configurations = getConfigForCurrentTest(method, configurations);
                if(configurations!=null){
                    for(int configNo = 0; configNo<configurations.length; configNo++){
                        Configuration config = configurations[configNo];
                        long startTime = System.currentTimeMillis();
                        extractParameters(unitTests, config);

                        unitTests.executor = Executors.newFixedThreadPool(unitTests.threadPool);
                        unitTests.output = new StringBuffer();
                        unitTests.allTestStepsFinished = Promise.promise();
                        unitTests.step = new AtomicInteger(0);
                        unitTests.doneTests = new AtomicInteger(0);
                        unitTests.testPhase = new AtomicBoolean(true);

                        if (unitTests.repetitions == 0) {
                            unitTests.allTestStepsFinished.success(null);
                        }
                        // Run test as many times as config.repetitions
                        for (int i = 0; i < unitTests.repetitions; i++) {
                            unitTests.step.incrementAndGet();
                            if(unitTests.sequential){
                                unitTests.currentTestFinished = Promise.promise();
                            }
                            // Test phase.
                            base.evaluate();
                            if(unitTests.sequential){
                                TestUtils.waitFuture(unitTests.currentTestFinished.future());
                            }
                        }
                        TestUtils.waitFuture(unitTests.allTestStepsFinished.future());
                        TestUtils.shutdown(unitTests.executor);

                        // Assert phase
                        unitTests.testPhase.set(false);
                        base.evaluate();
                        long endTime = System.currentTimeMillis();
                        System.out.printf("Test %s with  config no %d completed in %d milliseconds (%d repetitions)%n", method.getName(), configNo, endTime-startTime, config.repetitions());
                    }
                } else{
                    base.evaluate();
                }
            }

            private void extractParameters(BaseUnitTests unitTests, Configuration config) {
                unitTests.repetitions = config.repetitions();
                unitTests.threadPool = config.threadPool();
                unitTests.sequential = config.sequential();

                unitTests.sleep1 = config.sleep1().isEmpty()?null:Integer.parseInt(config.sleep1());
                unitTests.sleep2 = config.sleep2().isEmpty()?null:Integer.parseInt(config.sleep2());

                unitTests.fail1 = config.fail1().isEmpty()?null:config.fail1();
                unitTests.fail2 = config.fail2().isEmpty()?null:config.fail2();
                unitTests.fail3 = config.fail3().isEmpty()?null:config.fail3();
                unitTests.fail4 = config.fail4().isEmpty()?null:config.fail4();
                unitTests.fail5 = config.fail5().isEmpty()?null:config.fail5();
                unitTests.fail6 = config.fail6().isEmpty()?null:config.fail6();

                unitTests.option1 = config.option1().isEmpty()?null:config.option1();
                unitTests.option2 = config.option2().isEmpty()?null:config.option2();
                unitTests.option3 = config.option3().isEmpty()?null:config.option3();

            }

            private Configuration[] getConfigForCurrentTest(FrameworkMethod method, Configuration[] cfg) {
                Configurations configs2 = method.getAnnotation(Configurations.class);
                if(configs2!=null){
                    cfg = configs2.value();
                } else{
                    Configuration config2 = method.getAnnotation(Configuration.class);
                    if(config2!=null){
                        cfg = new Configuration[] {config2};
                    }
                }
                return cfg;
            }
        };
    }

}