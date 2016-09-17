package com.moss.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.moss.utilities.DateWatch;
import com.moss.utilities.Settings.UserName;
import com.moss.utilities.TimeWatch;

import static com.moss.utilities.Settings.*;

import static com.moss.json.Printer.*;


public class Parser {

    private final static JsonParser jsonParser = new JsonParser();

    // --------------------------------------------------------------------------------------------------------------------

    public static void importUsers() {
        for (UserName userName : UserName.values()) {
            JsonObject rootJson = getRootJson(userUrl + userName);
            int id = rootJson.get("_id").getAsInt();
            String name = rootJson.get("name").getAsString();
            String displayName = rootJson.get("display_name").getAsString();
            String bio = null;
            if (!rootJson.get("bio").isJsonNull()) {
                bio = rootJson.get("bio").getAsString();
            }
            String createdAt = rootJson.get("created_at").getAsString();
            createdAt = formatDate(createdAt);
            String updatedAt = rootJson.get("updated_at").getAsString();
            updatedAt = formatDate(updatedAt);
            StringBuilder output = new StringBuilder()
                    .append("\n id      : " + id)
                    .append("\n display : " + displayName)
                    .append("\n name    : " + name)
                    .append("\n bio     : " + bio)
                    .append("\n created : " + createdAt)
                    .append("\n updated : " + updatedAt)
                    .append(printDelimiter(true));
            printOutputToFile(output, twitchLogFile);
            printOutputToConsole(output);
        }
    }

    // --------------------------------------------------------------------------------------------------------------------

    public static void importSummary() {
        if (logMode) {
            JsonObject rootJson = getRootJson(summaryUrl);
            int channels = rootJson.get("channels").getAsInt();
            int viewers = rootJson.get("viewers").getAsInt();
            StringBuilder output = new StringBuilder()
                    .append(printDelimiter(false))
                    .append(printDate())
                    .append(printDelimiter(false))
                    .append("\n Channels : " + channels)
                    .append("\n Viewers  : " + viewers)
                    .append(printDelimiter(true));
            printOutputToFile(output, twitchLogFile);
        }
    }

    // --------------------------------------------------------------------------------------------------------------------

    public static void importFollows() {
        for (UserName userName : UserName.values()) {
            JsonObject rootJson = getRootJson(userUrl + userName + followsUrl);
            if (rootJson != null) {
                JsonArray follows = rootJson.get("follows").getAsJsonArray();
                if (logMode) {
                    StringBuilder out = new StringBuilder()
                            .append("\n\t\t\t\t\t\t" + userName)
                            .append("\n");
                    printOutputToFile(out, twitchLogFile);
                }
                for (JsonElement f : follows) {
                    JsonObject follow = f.getAsJsonObject();
                    String createdAt = follow.get("created_at").getAsString();
                    createdAt = formatDate(createdAt);
                    JsonObject followChannel = follow.get("channel").getAsJsonObject();
                    String followChannelName = followChannel.get("name").getAsString();
                    String followChannelNameOutput = formatString(followChannelName);
                    if (logMode) {
                        StringBuilder output = new StringBuilder()
                                .append("\n Follow : " + followChannelNameOutput)
                                .append(" | Date : " + createdAt);
                        printOutputToFile(output, twitchLogFile);
                    }
                    checkChatUsers(followChannelName);
                }
                if (logMode) {
                    printOutputToFile(printDelimiter(true), twitchLogFile);
                }
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------------------

    public static void importStreams() {
        if (normalMode) { 
            int streamsSize = 0;
            int viewersCount = 0;
            int offsets = 0;
            TimeWatch timeWatch = TimeWatch.start();
            do {
                JsonObject rootJson = getRootJson(allStreamsUrl + offsets);
                if (rootJson != null) {
                    JsonArray streams = rootJson.get("streams").getAsJsonArray();
                    streamsSize = streams.size();
                    if (streamsSize > 0) {
                        for (JsonElement s : streams) {
                            JsonObject stream = s.getAsJsonObject();
                            JsonObject channel = stream.get("channel").getAsJsonObject();
                            viewersCount = stream.get("viewers").getAsInt();
                            if (viewersCount > viewersLimit) {
                                String channelName = channel.get("name").getAsString();
                                checkChatUsers(channelName);
                            }
                        }
                    }
                    offsets += 100;
                }
            } while (streamsSize != 0 && viewersCount > viewersLimit);
            if (logMode) {
                StringBuilder output = new StringBuilder()
                        .append("\n Time : " + formatString(timeWatch.time()))
                        .append(" | Date : " + DateWatch.getCurrentDate())
                        .append(printDelimiter(true));
                printOutputToFile(output, twitchLogFile);
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------------------

    public static void importChannels() {
        for (ChannelUserName channelUserName : ChannelUserName.values()) {
            String channel = channelUserName.toString();
            JsonObject myChannelRootJson = getRootJson(tmiUrl + channel + chattersUrl);
            if (myChannelRootJson != null) {
                JsonObject myChatters = myChannelRootJson.get("chatters").getAsJsonObject();
                JsonArray myChatModerators = myChatters.get("moderators").getAsJsonArray();
                JsonArray myChatViewers = myChatters.get("viewers").getAsJsonArray();
                printChannelUsers(myChatModerators, channel);
                printChannelUsers(myChatViewers, channel);
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------------------

    public static JsonObject getRootJson(String spec) {
        JsonObject rootJson = null;
        try {
            URL url = new URL(spec);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.setRequestProperty("Client-ID", clientId);
            request.connect();
            JsonElement root = jsonParser.parse(new InputStreamReader((InputStream) request.getContent()));
            rootJson = root.getAsJsonObject();
        } catch (IOException e) {
            if (errorMode) {
                StringBuilder errorOutput = new StringBuilder()
                        .append(spec)
                        .append(" URL does not exist!");
                printOutputToFile(errorOutput, errorFile);
            }
        }
        return rootJson;
    }

    // --------------------------------------------------------------------------------------------------------------------

    public static void checkChatUsers(String channelName) {
        JsonObject channelRootJson = getRootJson(tmiUrl + channelName + chattersUrl);
        if (channelRootJson != null) {
            JsonObject chatters = channelRootJson.get("chatters").getAsJsonObject();
            JsonArray chatModerators = chatters.get("moderators").getAsJsonArray();
            JsonArray chatViewers = chatters.get("viewers").getAsJsonArray();
            printOnlineUser(chatModerators, channelName);
            printOnlineUser(chatViewers, channelName);
        }
    }



}
