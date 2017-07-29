## JUnit Report

#### This is a reporter for TestNG which generates the reports in xml format adhering to the JUnit test result schema for the Apache Ant JUnit and JUnitReport tasks.

The schema being used is referred from the GitHub repository: [JUnit-Schema](https://github.com/windyroad/JUnit-Schema).

To get started, just add the following as a maven dependency:

```xml
<dependency>
    <groupId>com.rationaleemotions</groupId>
    <artifactId>junitreport</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

Merely adding this artifact as a dependency should suffice. [Service loader mechanism](http://testng.org/doc/documentation-main.html#listeners-service-loader) is used to wire in this listener.
So there is no need to explicitly add the listener via the `<listener>` tag (or) via the `@Listeners` annotation.

Here's a sample of how a generated report can look like:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<testsuite name="SecondTest" timestamp="2017-07-29T20:16:12.846+05:30" hostname="necropolis.local" tests="4"
           failures="1" errors="2">
    <properties>
        <property name="errors" value="2"/>
        <property name="failures" value="1"/>
        <property name="ignored" value="0"/>
    </properties>
    <testcase name="@BeforeClass beforeClass" classname="com.rationaleemotions.AnotherApp" time="1"/>
    <testcase name="passTestMethod" classname="com.rationaleemotions.AnotherApp" time="5001"/>
    <testcase name="failTestMethod" classname="com.rationaleemotions.AnotherApp" time="2">
        <failure message="Simulating a failure" type="java.lang.AssertionError"/>
    </testcase>
    <testcase name="errorTestMethod" classname="com.rationaleemotions.AnotherApp" time="1">
        <error message="ERRORED due to : Simulating a failure" type="java.lang.RuntimeException">
            java.lang.RuntimeException: Simulating a failure
            at com.rationaleemotions.AnotherApp.errorTestMethod(AnotherApp.java:31)
            at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
            at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
            at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
            at java.lang.reflect.Method.invoke(Method.java:498)
            at org.testng.internal.MethodInvocationHelper.invokeMethod(MethodInvocationHelper.java:108)
            at org.testng.internal.Invoker.invokeMethod(Invoker.java:661)
            at org.testng.internal.Invoker.invokeTestMethod(Invoker.java:869)
            at org.testng.internal.Invoker.invokeTestMethods(Invoker.java:1193)
            at org.testng.internal.TestMethodWorker.invokeTestMethods(TestMethodWorker.java:126)
            at org.testng.internal.TestMethodWorker.run(TestMethodWorker.java:109)
            at org.testng.TestRunner.privateRun(TestRunner.java:744)
            at org.testng.TestRunner.run(TestRunner.java:602)
            at org.testng.SuiteRunner.runTest(SuiteRunner.java:380)
            at org.testng.SuiteRunner.runSequentially(SuiteRunner.java:375)
            at org.testng.SuiteRunner.privateRun(SuiteRunner.java:340)
            at org.testng.SuiteRunner.run(SuiteRunner.java:289)
            at org.testng.SuiteRunnerWorker.runSuite(SuiteRunnerWorker.java:52)
            at org.testng.SuiteRunnerWorker.run(SuiteRunnerWorker.java:86)
            at org.testng.TestNG.runSuitesSequentially(TestNG.java:1301)
            at org.testng.TestNG.runSuitesLocally(TestNG.java:1226)
            at org.testng.TestNG.runSuites(TestNG.java:1144)
            at org.testng.TestNG.run(TestNG.java:1115)
            at org.testng.IDEARemoteTestNG.run(IDEARemoteTestNG.java:72)
            at org.testng.RemoteTestNGStarter.main(RemoteTestNGStarter.java:123)
        </error>
    </testcase>
    <testcase name="skipTestMethod" classname="com.rationaleemotions.AnotherApp" time="0">
        <error message="SKIPPED due to : Method AnotherApp.skipTestMethod()[pri:0, instance:com.rationaleemotions.AnotherApp@483bf400] depends on not successfully finished methods"
               type="java.lang.Throwable">java.lang.Throwable: Method AnotherApp.skipTestMethod()[pri:0,
            instance:com.rationaleemotions.AnotherApp@483bf400] depends on not successfully finished methods
            at org.testng.internal.Invoker.invokeTestMethods(Invoker.java:1084)
            at org.testng.internal.TestMethodWorker.invokeTestMethods(TestMethodWorker.java:126)
            at org.testng.internal.TestMethodWorker.run(TestMethodWorker.java:109)
            at org.testng.TestRunner.privateRun(TestRunner.java:744)
            at org.testng.TestRunner.run(TestRunner.java:602)
            at org.testng.SuiteRunner.runTest(SuiteRunner.java:380)
            at org.testng.SuiteRunner.runSequentially(SuiteRunner.java:375)
            at org.testng.SuiteRunner.privateRun(SuiteRunner.java:340)
            at org.testng.SuiteRunner.run(SuiteRunner.java:289)
            at org.testng.SuiteRunnerWorker.runSuite(SuiteRunnerWorker.java:52)
            at org.testng.SuiteRunnerWorker.run(SuiteRunnerWorker.java:86)
            at org.testng.TestNG.runSuitesSequentially(TestNG.java:1301)
            at org.testng.TestNG.runSuitesLocally(TestNG.java:1226)
            at org.testng.TestNG.runSuites(TestNG.java:1144)
            at org.testng.TestNG.run(TestNG.java:1115)
            at org.testng.IDEARemoteTestNG.run(IDEARemoteTestNG.java:72)
            at org.testng.RemoteTestNGStarter.main(RemoteTestNGStarter.java:123)
        </error>
    </testcase>
    <system-out>Test message from beforeClass()
        Test message from testMethod()
    </system-out>
</testsuite>
```