package com.moss.utilities;

public class TimeWatch {

    private final static int SECONDS = 1000;
    private final static int MINUTES = 60;
    private long start;

    // --------------------------------------------------------------------------------------

    private TimeWatch() {
        reset();
    }

    // --------------------------------------------------------------------------------------

    public static TimeWatch start() {
        return new TimeWatch();
    }

    // --------------------------------------------------------------------------------------

    public TimeWatch reset() {
        start = System.currentTimeMillis();
        return this;
    }

    // --------------------------------------------------------------------------------------

    public String time() {
        long end = System.currentTimeMillis();
        long time = (end - start) / SECONDS;
        long minutes = time / MINUTES;
        long seconds = time % MINUTES;
        StringBuilder result = new StringBuilder()
                .append(minutes)
                .append("m")
                .append(seconds)
                .append("s");
        return result.toString();
    }

}
