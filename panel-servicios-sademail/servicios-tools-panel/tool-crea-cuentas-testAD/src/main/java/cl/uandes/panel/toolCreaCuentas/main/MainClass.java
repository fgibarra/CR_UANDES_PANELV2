package cl.uandes.panel.toolCreaCuentas.main;

import java.io.File;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import org.awaitility.Awaitility;
import static org.assertj.core.api.Assertions.*;

import org.apache.camel.main.Main;

public class MainClass {
	
	private static final long DURATION_MILIS = 10000;
	public static final String TRIGGER_FILE = "/tmp/panel/termino";
	private static Logger logger = Logger.getLogger(MainClass.class);
	
    public static void main(String[] args) throws Exception {
        // use Camels Main class
        Main main = new Main(MainClass.class);
        // and add all the XML routes
        main.configure().withRoutesIncludePattern("routes/*.xml");
        // turn on reloading routes on code-changes
        main.configure().withRoutesReloadEnabled(true);
        main.configure().withRoutesReloadDirectory("src/main/resources");
        main.configure().withRoutesReloadPattern("routes/*.xml");

        // now keep the application running until the JVM is terminated (ctrl + c or sigterm)
        main.run(args);
    }

    /*
    public static void mainAlternativo(String[] args) throws Exception {
		initLog4j();
		CamelContext context = new DefaultCamelContext(); 
        context.addRoutes(new MyCamelRoute()); 
  
        logger.info("Partiendo ruta....");
        context.start(); 
        File triggerFile = new File(TRIGGER_FILE);
        Awaitility.await().atMost(DURATION_MILIS, TimeUnit.MILLISECONDS).untilAsserted(() -> {
        	assertThat(triggerFile.exists()).isTrue();
        });
        logger.info("parando.....");
        context.stop();
	}
	*/
	//-----------------------------------------------------------------------------------------
	//
	//-----------------------------------------------------------------------------------------

	private static void initLog4j() {
		Properties props = new Properties();
		String datosLog = "toolCreaADtest.log";
		String dir = System.getProperty("jboss.server.log.dir");
		if (dir == null) {
			dir = "/tmp/panel/";
		}
		File f = new File(dir,datosLog);
		datosLog = f.getPath();

		props.put("log4j.rootLogger","INFO, A, console");
		props.put("log4j.appender.console", "org.apache.log4j.ConsoleAppender");
		props.put("log4j.appender.console.layout","org.apache.log4j.PatternLayout");
		props.put("log4j.appender.console.layout.ConversionPattern", "%d{dd/MM hh:mm:ss,SSS} %-5r %-5p [%l] %m%n");

		props.put("log4j.appender.A", "org.apache.log4j.RollingFileAppender");
		props.put("log4j.appender.A.Threshold","INFO");
		props.put("log4j.appender.A.ImmediateFlush","true");
		props.put("log4j.appender.A.File", datosLog);
		props.put("log4j.appender.A.Append","false");
		props.put("log4j.appender.A.layout","org.apache.log4j.PatternLayout");
		props.put("log4j.appender.A.layout.ConversionPattern", "%d{dd/MM hh:mm:ss,SSS} %-5r %-5p [%l] %m%n");

		PropertyConfigurator.configure(props);
	}

}
