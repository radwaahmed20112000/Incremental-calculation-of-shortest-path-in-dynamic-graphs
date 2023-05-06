package org.example;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class Client {

    private Client() {}

    private static String generateQuery() {
        Random random = new Random();
        String node1 = Integer.toString(random.nextInt(Integer.MAX_VALUE - 1) + 1);
        String node2 = Integer.toString(random.nextInt(Integer.MAX_VALUE - 1) + 1);
        
        String[] queries = new String[]{"A", "D", "Q"};
        int randomQuery = ThreadLocalRandom.current().nextInt(0, 3);
        return queries[randomQuery] + ' ' + node1 + ' ' + node2;
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

    private static String generateBatch(int quiresMaxNum, int batchNum) throws RemoteException {

        int randomNum = ThreadLocalRandom.current().nextInt(1, quiresMaxNum + 1);
        StringBuilder batch = new StringBuilder();

        for (int i = 0; i < randomNum; i++) {
            String query = generateQuery();
            batch.append(query);
            batch.append('\n');
        }
        batch.append('F');

        writeToFile(batch.toString(), batchNum);

        return batch.toString();
    }

    public static void main(String[] args) {

        try {
            // Connect to RMI Registry
            Registry registry = LocateRegistry.getRegistry("192.168.1.5", Registry.REGISTRY_PORT);
            BatchProcessing stub = (BatchProcessing) registry.lookup("BatchProcessing");

            for (int i = 0; i < 1; i++) {

                String batch = generateBatch(25, i);
                // Calculate Response Time
                long start = System.currentTimeMillis();
                List<Integer> results = stub.processBatch(batch);
                long end = System.currentTimeMillis();

                System.out.println("Response Time = " + (end - start) + " ms");
                // Print Output
                for (int result : results)
                    System.out.println(result);

                // Sleep till the next batch
                Thread.sleep(ThreadLocalRandom.current().nextInt(1, 11)* 1000L);
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e);
            e.printStackTrace();
        }
    }
}