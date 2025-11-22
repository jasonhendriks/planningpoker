package ca.hendriks.kotlinpoker

import io.cucumber.junit.platform.engine.Constants
import org.junit.platform.suite.api.*

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("specification")
@ConfigurationParameters(
    ConfigurationParameter(
        key = Constants.PLUGIN_PROPERTY_NAME,
        value = "html:target/cucumber-reports/CucumberIT.html"
    )
)
class CucumberIT
