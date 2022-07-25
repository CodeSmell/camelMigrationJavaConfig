package codesmell.camel;

import codesmell.util.LogFormatUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * replaces the CamelSpringMain in prior versions of this application
 *
 * The @ComponentScan replaces this part of the beans.xml
 *  <context:component-scan base-package="codesmell.config"/>
 */
@SpringBootApplication
@ComponentScan(basePackages = "codesmell.config")
public class CamelSpringBootMain {

    private static final Logger LOGGER = LoggerFactory.getLogger(CamelSpringBootMain.class);

    public static void main(String[] args) throws Exception {
        LogFormatUtil.logger(LOGGER)
                .withMessage("camel spring boot main...")
                .andLog(Level.INFO);

        SpringApplication.run(CamelSpringBootMain.class, args);
    }

}
