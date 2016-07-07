package com.moss.utilities;

public class Settings {

    // application settings
    public final static int viewersLimit = 250;
    public final static String logFile = "log.txt";
    public final static String twitchLogFile = "twitchLog.txt";
    public final static int delimiterSize = 75;

    public enum UserName {
        // users names
    }

    // api url list
    public final static String apiUrl = "https://api.twitch.tv/kraken/";
    public final static String userUrl = apiUrl + "users/";
    public final static String streamsUrl = apiUrl + "streams";
    public final static String summaryUrl = streamsUrl + "/summary";
    public final static String allStreamsUrl = streamsUrl + "?limit=100&offset=";
    public final static String followsUrl = "/follows/channels?limit=100";
    // tmi url list
    public final static String tmiUrl = "https://tmi.twitch.tv/group/user/";
    public final static String chattersUrl = "/chatters";

}
