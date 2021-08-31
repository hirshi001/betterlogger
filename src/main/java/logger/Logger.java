package logger;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Logger extends PrintStream{


    public static final int LOG = 0, WARN = 1, ERROR = 2;

    private final List<LoggerStringSupplier> stringSuppliers;
    private boolean debug, debugShort;
    private String debugColor, debugBefore, debugAfter;
    private PrintStream err;
    private final String name;

    public Logger(String name){
        this(name, System.out, System.err);
    }

    @SafeVarargs
    public Logger(String name, OutputStream out, PrintStream err, Supplier<String>... stringSuppliers){
        super(out);
        this.name = name;
        this.err = err;
        this.stringSuppliers = new ArrayList<>(stringSuppliers.length);
        for(Supplier<String> stringSupplier:stringSuppliers){
            this.stringSuppliers.add(new LoggerStringSupplier(stringSupplier));
        }
        debugColor = ConsoleColors.BLUE;
        debugBefore = "[";
        debugAfter = "] ";
        debug = false;
        debugShort = false;
    }

    @Override
    public void println(String message) {
        log(message,1);
    }

    @Override
    public void println(Object x) {
        log(x,1);
    }

    public void println(String message, int depth){
        log(message, depth+1);
    }

    @Override
    public void println(boolean x) {
        println(String.valueOf(x), 1);
    }

    @Override
    public void println(char x) {
        println(String.valueOf(x), 1);
    }

    @Override
    public void println(int x) {
        println(String.valueOf(x), 1);
    }

    @Override
    public void println(long x) {
        println(String.valueOf(x), 1);
    }

    @Override
    public void println(float x) {
        println(String.valueOf(x), 1);
    }

    @Override
    public void println(double x) {
        println(String.valueOf(x), 1);
    }

    @Override
    public void println(char[] x) {
        println(String.valueOf(x), 1);
    }

    public Logger log(Object object){
        String msg = object==null ? "null" : object.toString();
        logMessage(msg, 1);
        return this;
    }

    public Logger log(String message){
        logMessage(message, 1);
        return this;
    }

    public Logger log(String message, Object... args){
        logMessage(String.format(message, args), 1);
        return this;
    }

    public Logger log(Object object, int depth){
        String msg = object==null ? "null" : object.toString();
        logMessage(msg, depth+1);
        return this;
    }

    public Logger log(String message, int depth){
        logMessage(message, depth+1);
        return this;
    }

    public Logger log(String message, int depth, Object... args){
        logMessage(String.format(message, args), depth+1);
        return this;
    }

    public Logger warn(Object object){
        log(ConsoleColors.RED + "[WARN] " + ConsoleColors.RESET + object, 1);
        return this;
    }

    public Logger warn(String message){
        log(ConsoleColors.RED + "[WARN] " + ConsoleColors.RESET + message, 1);
        return this;
    }

    private Logger logMessage(String message, int depth){
        synchronized (this) {
            String[] lines = message.split("\n");
            StringBuilder builder = new StringBuilder();
            for (String line : lines) {
                builder.append(getLogMessage(depth + 1));
                builder.append(line);
                builder.append('\n');
            }
            super.print(builder.toString());
        }
        return this;
    }



    private StringBuilder getLogMessage(int depth){
        StringBuilder messageBuilder = new StringBuilder();
        for(LoggerStringSupplier loggerStringSupplier : stringSuppliers){
            if(loggerStringSupplier.enabled) messageBuilder.append(loggerStringSupplier.stringSupplier.get());
        }
        if(debug){
            messageBuilder.
                    append(debugColor).
                    append(debugBefore);

            StackTraceElement element = getLineNumber(depth+1);
            if(debugShort){
                messageBuilder.append(Logger.stackTraceElementToStringShort(element));
            }else{
                messageBuilder.append(element.toString());
            }

            messageBuilder.
                append(debugAfter).
                append(ConsoleColors.RESET);
        }


        return messageBuilder;
    }

    public Logger disable(Predicate<Supplier<String>> predicate){
        for(LoggerStringSupplier loggerStringSupplier : stringSuppliers){
            if(predicate.test(loggerStringSupplier.stringSupplier)){
                loggerStringSupplier.enabled = false;
            }
        }
        return this;
    }

    public Logger enable(Predicate<Supplier<String>> predicate){
        for(LoggerStringSupplier loggerStringSupplier : stringSuppliers) {
            if (predicate.test(loggerStringSupplier.stringSupplier)) {
                loggerStringSupplier.enabled = true;
            }
        }
        return this;
    }

    public Logger disable(int index){
        stringSuppliers.get(index).enabled = false;
        return this;
    }

    public Logger enable(int index){
        stringSuppliers.get(index).enabled = true;
        return this;
    }

    public Logger setOut(PrintStream out) {
        this.out = out;
        return this;
    }

    public Logger debug(){
        debug(true);
        return this;
    }

    public Logger debugShort(boolean debugShort){
        this.debugShort = debugShort;
        return this;
    }

    public Logger debug(boolean debug){
        debug(debug, ConsoleColors.RESET, "[", "] ");
        return this;
    }

    public Logger debug(String color){
        debug(true, color, "[", "] ");
        return this;

    }

    public Logger debug(String color, String before, String after){
        debug(true, color, before, after);
        return this;
    }

    public Logger debug(boolean debug, String color, String before, String after){
        this.debug = debug;
        this.debugColor = color;
        this.debugBefore = before;
        this.debugAfter = after;
        return this;
    }

    public boolean getDebug(){
        return debug;
    }

    public PrintStream getErr() {
        return err;
    }

    public Logger setErr(PrintStream err) {
        this.err = err;
        return this;
    }

    public String getName() {
        return name;
    }

    public static StackTraceElement getLineNumber(int depth){
        return Thread.currentThread().getStackTrace()[depth+2];
    }

    public static String stackTraceElementToStringShort(StackTraceElement element){


        StringBuilder messageBuilder = new StringBuilder();
        String clazz = element.getClassName();
        int index = clazz.lastIndexOf('.');
        clazz = clazz.substring(index+1);


        messageBuilder.append(clazz).append(".").append(element.getMethodName()).append(element.isNativeMethod() ? "(Native Method)" :
                (element.getFileName() != null && element.getLineNumber() >= 0 ?
                        "(" + element.getFileName() + ":" + element.getLineNumber() + ")" :
                        (element.getFileName() != null ? "(" + element.getFileName() + ")" : "(Unknown Source)")));
        return messageBuilder.toString();

    }

}

class LoggerStringSupplier{

    public Supplier<String> stringSupplier;
    public boolean enabled = true;

    public LoggerStringSupplier(Supplier<String> stringSupplier){
        this.stringSupplier = stringSupplier;
    }

}
