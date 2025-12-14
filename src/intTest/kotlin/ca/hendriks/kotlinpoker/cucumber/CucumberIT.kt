package ca.hendriks.kotlinpoker.cucumber

import io.cucumber.junit.platform.engine.Constants
import org.junit.platform.suite.api.ConfigurationParameter
import org.junit.platform.suite.api.ConfigurationParameters
import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectPackages
import org.junit.platform.suite.api.Suite

@Suite
@IncludeEngines("cucumber")
@SelectPackages("specification")
@ConfigurationParameters(
    ConfigurationParameter(
        key = Constants.PLUGIN_PROPERTY_NAME,
        value = "html:target/cucumber-reports/CucumberIT.html"
    )
)
class CucumberIT
