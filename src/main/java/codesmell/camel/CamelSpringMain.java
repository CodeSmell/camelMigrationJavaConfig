package codesmell.camel;

import codesmell.util.LogFormatUtil;
import org.apache.camel.spring.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CamelSpringMain {
    private static final Logger LOGGER = LoggerFactory.getLogger(CamelSpringMain.class);

    public static void main(String[] args) throws Exception {
        LogFormatUtil.logger(LOGGER)
            .withMessage("camel spring main with java config...")
            .andLog(Level.INFO);

        boot();
    }

    /**
     * Camel Spring Main (Java-Config)
     */
    public static void boot() throws Exception {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("META-INF/spring/beans.xml");
        Main camelSpringMain = new Main();
        camelSpringMain.setApplicationContext((AbstractApplicationContext) ctx);
        camelSpringMain.run();
    }
}
