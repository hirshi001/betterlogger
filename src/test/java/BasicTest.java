import logger.ConsoleColors;
import logger.DateStringFunction;
import logger.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.Field;
import java.util.Map;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BasicTest {

    Logger logger;


    @BeforeEach
    public void setup(){
        logger = new Logger(System.out, System.err,
                new DateStringFunction(ConsoleColors.GREEN, "[", "] "),
                ()-> (ConsoleColors.PURPLE + "[SERVER] " + ConsoleColors.RESET));

    }

    @Test
    public void BasicTest(){
        logger.log("test one");
        System.out.println("HI");
        logger.log("test two");
    }

    @Test
    public void NewLineTest(){
        logger.log("new line test\nnew line");
    }

    @Test
    public void ThreadSleepTest() throws InterruptedException {
        for(int i=0;i<5;i++){
            logger.log(" " + i);
            Thread.sleep(500);
        }
    }

    @Test
    public void CallingAMethodTest(){
        methodCallTest();
    }

    private void methodCallTest(){
        logger.log("method call test");
    }

    @Test
    public void debugTest(){
        logger.log("pre debug call");
        logger.debug(ConsoleColors.BLUE);
        logger.debugShort(true);
        logger.log("post debug call");
        logger.log("send post debug call");
        logger.debug(false);
        logger.log("debug disabled");
        logger.log("debug disabled second call");
    }

    @Test
    public void setSystemOut(){
        System.setOut(logger);
        soutPrint();
        System.out.println();
        logger.debug(ConsoleColors.BLUE);
        soutPrint();
    }

    private void soutPrint(){
        System.out.println("This is a System.setOut(logger) test");
        System.out.println(5);
        System.out.println(true);
        System.out.println('a');
        System.out.println(5.0D);
        System.out.println(3L);
        System.out.println(31F);
        System.out.println(new Object());
    }

    @Test
    public void warnTest(){
        logger.warn("Warning");
    }


}
