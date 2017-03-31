package cloud.operon.platform.cucumber.stepdefs;

import cloud.operon.platform.OperonCloudPlatformApp;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.boot.test.context.SpringBootTest;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = OperonCloudPlatformApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
