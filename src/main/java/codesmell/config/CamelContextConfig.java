package codesmell.config;

import codesmell.util.LogFormatUtil;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CamelContextConfig extends CamelConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(CamelContextConfig.class);

    @Autowired
    CamelRoutesConfig camelRoutesConfig;

    @Override
    protected void setupCamelContext(CamelContext camelContext) throws Exception {
        LogFormatUtil.logger(LOGGER)
            .withMessage("configuring camel context...")
            .andLog(Level.INFO);

        super.setupCamelContext(camelContext);
    }

    @Override
    public List<RouteBuilder> routes() {
        LogFormatUtil.logger(LOGGER)
            .withMessage("routes are under construction...")
            .andLog(Level.DEBUG);
        return Arrays.asList(
            camelRoutesConfig.gozerFileRoute(),
            camelRoutesConfig.gozerTxSetRoute(),
            camelRoutesConfig.gozerEndRoute());
    }
}
