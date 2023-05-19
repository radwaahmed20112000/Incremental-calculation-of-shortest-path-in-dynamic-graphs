package org.example;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    private static List<Integer> generateRandomNumsWithPercentage(int percent, int quiresNum) {

        List<Integer> randoms = new ArrayList<>();
        Random rand = new Random();

        int addDeleteOps = (int) (quiresNum * (double) percent /100);
        for (int i = 0; i < addDeleteOps; i++)
            randoms.add(rand.nextInt(2));
        for (int i = addDeleteOps; i < quiresNum; i++)
            randoms.add(2);

        Collections.shuffle(randoms);
        return randoms;
    }

    private static String generateQuery(int query) {
        Random random = new Random();
        String node1 = Integer.toString(random.nextInt(150 - 1) + 1);
        String node2 = Integer.toString(random.nextInt(150 - 1) + 1);

        String[] queries = new String[]{"A", "D", "Q"};
        if(query == -1)
            query = ThreadLocalRandom.current().nextInt(0, 3);
        return queries[query] + ' ' + node1 + ' ' + node2;
    }

    private static void writeToFile(String batch, String path) {
        pathCheck(path);
        try {
            Files.writeString(Path.of(path), batch,
                    StandardCharsets.UTF_8);
        }
        catch (IOException ex) {
            System.out.print("Invalid Path");
        }
    }

    public static String generateBiasedBatch(String path, int quiresNum, int percent) {

        StringBuilder batch = new StringBuilder();

        List<Integer> randomQuires = generateRandomNumsWithPercentage(percent, quiresNum);
        for (int i = 0; i < quiresNum; i++) {
            String query = generateQuery(randomQuires.get(i));
            batch.append(query);
            batch.append('\n');
        }
        batch.append('F');

        writeToFile(batch.toString(), path);

        return batch.toString();
    }
    public static void pathCheck(String path) {
        File file = new File(path);

        // Get the parent directory
        File parentDir = file.getParentFile();

        // Create the parent directory and any missing ancestors
        if (!parentDir.exists()) {
            boolean success = parentDir.mkdirs();
            if (!success) {
                // Failed to create the directory
                throw new RuntimeException("Failed to create directory: " + parentDir.getAbsolutePath());
            }
        }
    }
}
