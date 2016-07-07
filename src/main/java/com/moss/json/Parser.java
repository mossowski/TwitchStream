package com.moss.json;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
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
                    .append("\n id           : " + id)
                    .append("\n display Name : " + displayName)
                    .append("\n name         : " + name)
                    .append("\n bio          : " + bio)
                    .append("\n created at   : " + createdAt)
                    .append("\n updated at   : " + updatedAt)
                    .append(printDelimiter(true));
            printOutputToFile(output, twitchLogFile);
            printOutputToConsole(output);
        }
    }

    // --------------------------------------------------------------------------------------------------------------------

    public static void importSummary() {
        JsonObject rootJson = getRootJson(summaryUrl);
        int channels = rootJson.get("channels").getAsInt();
        int viewers = rootJson.get("viewers").getAsInt();
        StringBuilder output = new StringBuilder()
                .append(printDelimiter(false))
                .append(printDate())
                .append(printDelimiter(false))
                .append("\n Channels     : " + channels)
                .append("\n Viewers      : " + viewers)
                .append(printDelimiter(true));
        printOutputToFile(output, twitchLogFile);
    }

    // --------------------------------------------------------------------------------------------------------------------

    public static void importFollows() {
        for (UserName userName : UserName.values()) {
            JsonObject rootJson = getRootJson(userUrl + userName + followsUrl);
            JsonArray follows = rootJson.get("follows").getAsJsonArray();
            StringBuilder out = new StringBuilder()
                    .append("\n\t\t\t\t\t\t" + userName)
                    .append("\n");
            printOutputToFile(out, twitchLogFile);
            for (JsonElement f : follows) {
                JsonObject follow = f.getAsJsonObject();
                String createdAt = follow.get("created_at").getAsString();
                createdAt = formatDate(createdAt);
                JsonObject followChannel = follow.get("channel").getAsJsonObject();
                String followChannelName = followChannel.get("name").getAsString();
                String followChannelNameOutput = formatString(followChannelName);
                StringBuilder output = new StringBuilder()
                        .append("\n Follow       : " + followChannelNameOutput)
                        .append("|    Date       : " + createdAt);
                printOutputToFile(output, twitchLogFile);
                checkChatUsers(followChannelName);
            }
            printOutputToFile(printDelimiter(true), twitchLogFile);
        }
    }

    // --------------------------------------------------------------------------------------------------------------------

    public static void importStreams() {
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
        StringBuilder output = new StringBuilder()
                .append("\n Time         : " + formatString(timeWatch.time()))
                .append("|    Date       : " + DateWatch.getCurrentDate())
                .append(printDelimiter(true));
        printOutputToFile(output, twitchLogFile);
    }

    // --------------------------------------------------------------------------------------------------------------------

    public static JsonObject getRootJson(String spec) {
        JsonObject rootJson = null;
        try {
            URL url = new URL(spec);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();
            JsonElement root = jsonParser.parse(new InputStreamReader((InputStream) request.getContent()));
            rootJson = root.getAsJsonObject();
        } catch (IOException e) {
            System.out.println(spec + " URL does not exist!");
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

    // --------------------------------------------------------------------------------------------------------------------

    public static void printOnlineUser(JsonArray chatViewers, String channelName) {
        for (JsonElement chatViewer : chatViewers) {
            String chatViewerName = chatViewer.getAsString();
            for (UserName userName : UserName.values()) {
                if (chatViewerName.equals(userName.toString())) {
                    String chatViewerOutput = formatString(chatViewerName);
                    String date = DateWatch.getCurrentDate();
                    StringBuilder output = new StringBuilder()
                            .append("\n Username     : " + chatViewerOutput)
                            .append("|    Date       : " + date)
                            .append("     Channel    : " + channelName);
                    printOutputToFile(output, twitchLogFile);
                    printOutputToFile(output, logFile);
                    printOutputToConsole(output);
                }
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------------------

    public static void printOutputToFile(StringBuilder output, String fileName) {
        try (FileWriter fileWriter = new FileWriter(fileName, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                PrintWriter printWriter = new PrintWriter(bufferedWriter)) {
            printWriter.print(output);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    // --------------------------------------------------------------------------------------------------------------------

    public static void printOutputToConsole(StringBuilder output) {
        System.out.println(output);
    }

    // --------------------------------------------------------------------------------------------------------------------

    public static StringBuilder printDelimiter(boolean isDefault) {
        StringBuilder delimiter = new StringBuilder("\n");
        for (int i = 0; i < delimiterSize; i++) {
            if (isDefault)
                delimiter.append("-");
            else
                delimiter.append("=");
        }
        return delimiter;
    }

    // --------------------------------------------------------------------------------------------------------------------

    public static String printDate() {
        String date = " " + DateWatch.getCurrentDate() + " ";
        StringBuilder result = new StringBuilder("\n");
        int delimiter = (delimiterSize - date.length()) / 2;
        for (int i = 0; i < delimiter; i++) {
            result.append("=");
        }
        result.append(date);
        for (int i = 0; i < delimiter; i++) {
            result.append("=");
        }
        return result.toString();
    }

    // --------------------------------------------------------------------------------------------------------------------

    public static String formatDate(String date) {
        String result = date.replace("T", " ").replace("Z", "");
        return result;
    }

    // --------------------------------------------------------------------------------------------------------------------

    public static String formatString(String string) {
        String result = string;
        int stringLength = string.length();
        if (stringLength < 20) {
            int missingSpaces = 20 - stringLength;
            for (int i = 0; i < missingSpaces; i++) {
                result += " ";
            }
        }
        return result;
    }

}
