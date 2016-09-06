package com.moss.json;

import static com.moss.utilities.Settings.delimiterSize;
import static com.moss.utilities.Settings.stringSize;
import static com.moss.utilities.Settings.errorFile;
import static com.moss.utilities.Settings.errorMode;
import static com.moss.utilities.Settings.logFile;
import static com.moss.utilities.Settings.logMode;
import static com.moss.utilities.Settings.twitchLogFile;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.moss.utilities.DateWatch;
import com.moss.utilities.Settings.UserName;

public class Printer {

    // --------------------------------------------------------------------------------------------------------------------

    public static void printOnlineUser(JsonArray chatViewers, String channelName) {
        for (JsonElement chatViewer : chatViewers) {
            String chatViewerName = chatViewer.getAsString();
            for (UserName userName : UserName.values()) {
                if (chatViewerName.equals(userName.toString())) {
                    String chatViewerOutput = formatString(chatViewerName);
                    String date = DateWatch.getCurrentDate();
                    StringBuilder output = new StringBuilder()
                            .append("\n Username : " + chatViewerOutput)
                            .append(" | Date : " + date)
                            .append(" | Channel : " + channelName);
                    if (logMode) {
                        printOutputToFile(output, twitchLogFile);
                    }
                    printOutputToFile(output, logFile);
                    printOutputToConsole(output);
                }
            }
        }
    }

    // --------------------------------------------------------------------------------------------------------------------

    public static void printChannelUsers(JsonArray chatViewers, String channelName) {
        for (JsonElement chatViewer : chatViewers) {
            String chatViewerName = chatViewer.getAsString();
            String chatViewerOutput = formatString(chatViewerName);
            String date = DateWatch.getCurrentDate();
            StringBuilder output = new StringBuilder()
                        .append("\n Username : " + chatViewerOutput)
                        .append(" | Date : " + date)
                        .append(" | Channel : " + channelName);
           if (logMode) {
               printOutputToFile(output, twitchLogFile);
           }
           printOutputToFile(output, logFile);
           printOutputToConsole(output);
        }
    }

    // --------------------------------------------------------------------------------------------------------------------

    public static void printOutputToFile(StringBuilder output, String fileName) {
        try (FileWriter fileWriter = new FileWriter(fileName, true);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                PrintWriter printWriter = new PrintWriter(bufferedWriter)) {
            printWriter.print(output);
        } catch (IOException e) {
            if (errorMode) {
                StringBuilder errorOutput = new StringBuilder()
                        .append(e.getMessage());
                printOutputToFile(errorOutput, errorFile);
            }
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
        if (stringLength < stringSize) {
            int missingSpaces = stringSize - stringLength;
            for (int i = 0; i < missingSpaces; i++) {
                result += " ";
            }
        }
        return result;
    }

}
