package com.moss;

import com.moss.json.Parser;

public class App {

    public static void main(String[] args) {
        Parser.importSummary();
        Parser.importUsers();

        while (true) {
            Parser.importFollows();
            Parser.importChannels();
            Parser.importStreams();
        }
    }

}
