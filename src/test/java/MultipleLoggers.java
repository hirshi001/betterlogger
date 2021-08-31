import logger.ConsoleColors;
import logger.DateStringFunction;
import logger.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class MultipleLoggers {

    @BeforeEach
    public void init(){
        BasicTest.resetLoggerMappings();
    }


    @Test
    public void MultipleLoggersTest1(){
        Logger logger1 = new Logger("logger1", System.out, System.err,
                ()->(ConsoleColors.BLACK + "[Logger 1] " + ConsoleColors.RESET),
                new DateStringFunction(ConsoleColors.CYAN, "[", "] "));
        Logger logger2 = new Logger("xf8b", System.out, System.err,
                new DateStringFunction(ConsoleColors.BLUE, "[", "] "),
                ()->ConsoleColors.YELLOW + "xf8b: " + ConsoleColors.RESET);
        Logger logger3 = new Logger("sparky", System.out, System.err,
                new DateStringFunction(ConsoleColors.BLUE, "[", "] "),
                ()-> ConsoleColors.PURPLE+ "sparky: " + ConsoleColors.RESET);

        logger1.debug(ConsoleColors.BLUE, "[", "]: ");
        logger1.log("this is logger 1");
        logger2.log("hi sparky");
        logger3.log("Hi xf");
    }
}
