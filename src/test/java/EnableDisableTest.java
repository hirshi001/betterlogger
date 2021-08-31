import logger.ConsoleColors;
import logger.DateStringFunction;
import logger.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EnableDisableTest {

    Logger logger1;

    @BeforeEach
    public void init(){
        logger1 = new Logger(System.out, System.err,
                ()->(ConsoleColors.BLACK + "[Logger 1] " + ConsoleColors.RESET),
                new DateStringFunction(ConsoleColors.CYAN, "[", "] "));
    }

    @Test
    public void disableInstanceOfTest(){

        logger1.log("Disable Instance Of Test");
        logger1.disable(sup -> sup instanceof DateStringFunction);
        logger1.log("Date String Function disabled");

    }

    @Test
    public void disableIndexTest(){

        logger1.log("Disable Index 1 Test");
        logger1.disable(1);
        logger1.log("Date String Function disabled");

    }

    @Test
    public void enableIndexTest(){
        logger1.log("Calling disableIndexTest()");
        disableIndexTest();
        logger1.log("Going to enable DateStringFunction");
        logger1.enable(1);
        logger1.log("DateStringFunction enabled");
    }


}
