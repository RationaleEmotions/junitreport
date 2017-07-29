package com.rationaleemotions;

import com.rationaleemotions.junit.ObjectFactory;
import com.rationaleemotions.junit.Testsuite;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.internal.Utils;
import org.testng.xml.XmlSuite;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A JUnit reporter that adheres to JUnit test result schema for the Apache Ant JUnit and JUnitReport tasks.
 * The schema that is used is referred from <a href="https://github.com/windyroad/JUnit-Schema">here</a>.
 */
public class JUnitReport implements IReporter {
    private ObjectFactory factory = new ObjectFactory();
    private Map<String, String> fileNameMap = new HashMap<>();
    private StringBuilder builder;
    private int fileNameIncrementer = 0;
    private int errors = 0;
    private int failures = 0;

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        for (ISuite suite : suites) {
            Collection<ISuiteResult> suiteResults = suite.getResults().values();
            for (ISuiteResult suiteResult : suiteResults) {
                generateReport(suiteResult.getTestContext());
            }
        }
    }

    private void generateReport(ITestContext context) {
        builder = new StringBuilder();
        List<ITestResult> results = new ArrayList<>();
        Testsuite testsuite = factory.createTestsuite();
        testsuite.setTests(context.getAllTestMethods().length);
        testsuite.setName(context.getName());

        try {
            testsuite.setTimestamp(time());
            testsuite.setHostname(InetAddress.getLocalHost().getHostName());
        } catch (DatatypeConfigurationException | UnknownHostException e) {
            //NOSONAR
        }
        results.addAll(context.getPassedConfigurations().getAllResults());
        results.addAll(context.getFailedConfigurations().getAllResults());
        results.addAll(context.getSkippedConfigurations().getAllResults());

        results.addAll(context.getPassedTests().getAllResults());
        results.addAll(context.getFailedTests().getAllResults());
        results.addAll(context.getFailedButWithinSuccessPercentageTests().getAllResults());
        results.addAll(context.getSkippedTests().getAllResults());

        constructTestCases(testsuite, results);
        testsuite.setErrors(errors);
        testsuite.setFailures(failures);
        testsuite.setProperties(properties(context));
        testsuite.setSystemOut(builder.toString());

        writeToFile(testsuite, generateFileName(context));
        builder = null;
        errors = 0;
        failures = 0;
    }

    private void constructTestCases(Testsuite testsuite, List<ITestResult> results) {
        for (ITestResult result : results) {
            List<String> output = Reporter.getOutput(result);
            for (String each : output) {
                builder.append(each).append("\n");
            }
            testsuite.getTestcase().add(constructTestCase(result));
        }
    }

    private Testsuite.Testcase constructTestCase(ITestResult result) {
        Testsuite.Testcase testcase = factory.createTestsuiteTestcase();
        testcase.setClassname(result.getMethod().getRealClass().getName());
        long time = result.getEndMillis() - result.getStartMillis();
        testcase.setTime(BigDecimal.valueOf(time));
        if (result.getMethod().isTest()) {
            testcase.setName(result.getName());
        } else {
            testcase.setName(Utils.detailedMethodName(result.getMethod(), false));
        }
        if (result.isSuccess()) {
            return testcase;
        }
        Throwable throwable = result.getThrowable();
        if (throwable instanceof AssertionError) {
            failures++;
            Testsuite.Testcase.Failure failure = factory.createTestsuiteTestcaseFailure();
            if (throwable != null) {
                failure.setMessage(throwable.getMessage());
                failure.setType(throwable.getClass().getName());
            }
            testcase.setFailure(failure);
        } else {
            errors++;
            Testsuite.Testcase.Error error = factory.createTestsuiteTestcaseError();
            String prefix = "ERRORED";
            if (ITestResult.SKIP == result.getStatus()) {
                prefix = "SKIPPED";
            }
            String msg = prefix;
            if (throwable != null) {
                msg = msg + " due to : " + throwable.getMessage();
                error.setType(throwable.getClass().getName());
                error.setValue(Utils.shortStackTrace(throwable, false));
            }
            error.setMessage(msg);
            testcase.setError(error);
        }
        return testcase;
    }

    private void writeToFile(Testsuite testsuite, String fileName) {
        try {
            JAXBContext jaxContext = JAXBContext.newInstance(Testsuite.class);
            Marshaller jaxbMarshaller = jaxContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(factory.createTestsuite(testsuite), new File(fileName));
        } catch (JAXBException e) {
            throw new RuntimeException("Report generation failed. Root cause :" + e.getMessage(), e);
        }
    }

    private String generateFileName(ITestContext context) {
        File dir = new File(context.getOutputDirectory());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName;
        String keyToSearch = context.getSuite().getName() + context.getName();
        if (fileNameMap.get(keyToSearch) == null) {
            fileName = context.getName();
        } else {
            fileName = context.getName() + fileNameIncrementer++;
        }

        fileNameMap.put(keyToSearch, fileName);
        return dir.getAbsolutePath() + File.separator + fileName + ".xml";
    }


    private Testsuite.Properties properties(ITestContext context) {
        Testsuite.Properties properties = factory.createTestsuiteProperties();
        properties.getProperty().add(newProperty("errors", Integer.toString(errors)));
        properties.getProperty().add(newProperty("failures", Integer.toString(failures)));
        properties.getProperty().add(newProperty("ignored", Integer.toString(context.getExcludedMethods().size())));
        return properties;
    }

    private XMLGregorianCalendar time() throws DatatypeConfigurationException {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTime(Calendar.getInstance().getTime());
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
    }

    private Testsuite.Properties.Property newProperty(String name, String value) {
        Testsuite.Properties.Property property = factory.createTestsuitePropertiesProperty();
        property.setName(name);
        property.setValue(value);
        return property;
    }
}
