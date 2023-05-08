package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;

public class Utils {

    private static int[] generateRandomNumsWithPercentage(int percent, int quiresNum) {

        int[] randoms = new int[quiresNum];
        Random rand = new Random();

        for (int i = 0; i < quiresNum; i++) {
            if (rand.nextDouble() < (double) percent /100)
                randoms[i] = rand.nextInt(2);
            else
                randoms[i] = rand.nextInt(3);
        }
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

        int[] randomQuires = generateRandomNumsWithPercentage(percent, quiresNum);
        for (int i = 0; i < quiresNum; i++) {
            String query = generateQuery(randomQuires[i]);
            batch.append(query);
            batch.append('\n');
        }
        batch.append('F');

        writeToFile(batch.toString(), batchNum);

        return batch.toString();
    }
}
