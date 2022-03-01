package logger;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.*;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Logger extends PrintStream{


    public static final int LOG = 0, WARN = 1, ERROR = 2;

    private final List<LoggerStringSupplier> stringSuppliers;
    private boolean debug, debugShort;
    private String debugColor, debugBefore, debugAfter;
    private PrintStream err;
    private int logLevel = LOG;

    public Logger(){
        this(System.out, System.err);
    }

    //test comment
    @SafeVarargs
    public Logger(OutputStream out, PrintStream err, Supplier<String>... stringSuppliers){
        super(out);
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

    public Logger logLevel(int logLevel){
        this.logLevel = logLevel;
        return this;
    }

    @Override
    public void println(String message) {
        log0(message + '\n',1);
    }

    @Override
    public void println(Object x) {
        log0(String.valueOf(x) + '\n',1);
    }

    public void println(String message, int depth){
        log0(message + '\n', depth+1);
    }

    @Override
    public void println(boolean x) {
        log0(String.valueOf(x) + '\n', 1);
    }

    @Override
    public void println(char x) {
        log0(String.valueOf(x) + '\n', 1);
    }

    @Override
    public void println(int x) {
        log0(String.valueOf(x) + '\n', 1);
    }

    @Override
    public void println(long x) {
        log0(String.valueOf(x) + '\n', 1);
    }

    @Override
    public void println(float x) {
        log0(String.valueOf(x) + '\n', 1);
    }

    @Override
    public void println(double x) {
        log0(String.valueOf(x) + '\n', 1);
    }

    @Override
    public void println(char[] x) {
        log0(String.valueOf(x) + '\n', 1);
    }

    public Logger log(Object object){
        if(!checkLog(LOG)) return this;
        log0(String.valueOf(object), 1);
        return this;
    }

    public Logger log(String message){
        if(!checkLog(LOG)) return this;
        log0(message, 1);
        return this;
    }

    public Logger log(String message, Object... args){
        if(!checkLog(LOG)) return this;
        log0(String.format(message, args), 1);
        return this;
    }

    public Logger log(Object object, int depth){
        if(!checkLog(LOG)) return this;
        log0(String.valueOf(object), depth+1);
        return this;
    }

    public Logger log(String message, int depth){
        if(!checkLog(LOG)) return this;
        log0(message, depth+1);
        return this;
    }

    public Logger log(String message, int depth, Object... args){
        if(!checkLog(LOG)) return this;
        log0(String.format(message, args), depth+1);
        return this;
    }

    public Logger warn(Object object){
        if(!checkLog(WARN)) return this;
        warn0(object.toString(), 1);
        return this;
    }

    public Logger warn(String message){
        if(!checkLog(WARN)) return this;
        warn0(message, 1);
        return this;
    }

    public Logger warn(Object object, int depth){
        if(!checkLog(WARN)) return this;
        warn0(object.toString(), depth);
        return this;
    }

    public Logger warn(String message, int depth){
        if(!checkLog(WARN)) return this;
        warn0(message, depth+1);
        return this;
    }

    private void warn0(String message, int depth){
        log0(ConsoleColors.RED + "[WARN] " + ConsoleColors.RESET + message, depth+1);
    }

    private Logger log0(String message, int depth){
        if(!checkLog(LOG)) return this;
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

    public Logger error(Object object){
        if(!checkLog(ERROR)) return this;
        error0(object.toString(), 1);
        return this;
    }

    public Logger error(String message){
        if(!checkLog(ERROR)) return this;
        error0(message, 1);
        return this;
    }

    public Logger error(Object object, int depth){
        if(!checkLog(ERROR)) return this;
        error0(object.toString(), depth+1);
        return this;
    }

    public Logger error(String message, int depth){
        if(!checkLog(ERROR)) return this;
        error0(message, depth+1);
        return this;
    }

    private void error0(String message, int depth){
        log0(ConsoleColors.RED + "[ERROR] " + ConsoleColors.RESET + message, depth+1);
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
        this.debug = debug;
        return this;
    }

    public Logger debug(String color){
        this.debug = debug;
        this.debugColor = color;
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

    public boolean getDebugShort(){
        return debugShort;
    }

    public PrintStream getErr() {
        return err;
    }

    public Logger setErr(PrintStream err) {
        this.err = err;
        return this;
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

    private boolean checkLog(int logLevel){
        return logLevel >= this.logLevel;
    }

}

class LoggerStringSupplier{

    public Supplier<String> stringSupplier;
    public boolean enabled = true;

    public LoggerStringSupplier(Supplier<String> stringSupplier){
        this.stringSupplier = stringSupplier;
    }

}
