package spypunk.snake.service;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class HighScoreManager {
    private static final String FILE_NAME = "highscore.txt";

    public static int getHighScore() {
        try {
            if (!Files.exists(Paths.get(FILE_NAME))) {
                return 0;
            }
            BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));
            String line = reader.readLine();
            reader.close();
            return Integer.parseInt(line);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static void saveHighScore(int score) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME));
            writer.write(String.valueOf(score));
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
