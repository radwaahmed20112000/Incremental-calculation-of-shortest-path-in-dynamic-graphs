package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

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
        String node1 = Integer.toString(random.nextInt(Integer.MAX_VALUE - 1) + 1);
        String node2 = Integer.toString(random.nextInt(Integer.MAX_VALUE - 1) + 1);

        String[] queries = new String[]{"A", "D", "Q"};
        if(query == -1)
            query = ThreadLocalRandom.current().nextInt(0, 3);
        return queries[query] + ' ' + node1 + ' ' + node2;
    }

    private static void writeToFile(String batch, int i) {
        try {
            Files.writeString(Path.of("batch_" + i), batch,
                    StandardCharsets.UTF_8);
        }
        catch (IOException ex) {
            System.out.print("Invalid Path");
        }
    }

    public static String generateBatch(int quiresNum, int batchNum) {
        StringBuilder batch = new StringBuilder();

        for (int i = 0; i < quiresNum; i++) {
            String query = generateQuery(-1);
            batch.append(query);
            batch.append('\n');
        }
        batch.append('F');

        writeToFile(batch.toString(), batchNum);

        return batch.toString();
    }
    public static String generateBiasedBatch(int quiresNum, int batchNum, int percent) {

        StringBuilder batch = new StringBuilder();

        List<Integer> randomQuires = generateRandomNumsWithPercentage(percent, quiresNum);
        for (int i = 0; i < quiresNum; i++) {
            String query = generateQuery(randomQuires.get(i));
            batch.append(query);
            batch.append('\n');
        }
        batch.append('F');

        writeToFile(batch.toString(), batchNum);

        return batch.toString();
    }
}
