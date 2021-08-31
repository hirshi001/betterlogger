package logger;

import java.util.Date;
import java.util.function.Supplier;

public class DateStringFunction implements Supplier<String> {

    private final Date date = new Date();
    private final String color, beginning, end;

    public DateStringFunction(){
        this(ConsoleColors.RESET, "(", ") ");
    }

    public DateStringFunction(String color, String beginning, String end){
        this.color = color;
        this.beginning = beginning;
        this.end = end;
    }



    @Override
    public String get() {
        date.setTime(System.currentTimeMillis());
        return color + beginning + date.toString() + end + ConsoleColors.RESET;
    }
}
