package com.moss;

import static com.moss.utilities.Settings.allFollowMode;
import static com.moss.utilities.Settings.normalMode;

import com.moss.json.Parser;

public class App {

    public static void main(String[] args) {
        Parser.importSummary();
        Parser.importUsers();

        while (true) {
            Parser.importChannels();
            if (allFollowMode) {
                Parser.importAllFollows();
            }
            else {
                Parser.importFollows();
            }
            if (normalMode) {
                Parser.importStreams();
            }
        }
    }

}
