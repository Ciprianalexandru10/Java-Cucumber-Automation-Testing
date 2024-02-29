package testRunner;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.AfterClass;
import org.junit.runner.RunWith;
import net.masterthought.cucumber.ReportBuilder;
import net.masterthought.cucumber.Configuration;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RunWith(Cucumber.class)
@CucumberOptions(
    features = {"features"},
    glue =  {"steps"},
    plugin = {"json:target/cucumber-reports/cucumber.json"}
)
public class TestRunner {
    @AfterClass
    public static void generateReport() {
        List<String> jsonFiles = new ArrayList<>();
        jsonFiles.add("target/cucumber-reports/cucumber.json");

        Configuration configuration = new Configuration(new File("target/cucumber-reports"), "Cucumber Test Report");
        configuration.setBuildNumber("1.0.0");

        ReportBuilder reportBuilder = new ReportBuilder(jsonFiles, configuration);
        reportBuilder.generateReports();
    }
}

