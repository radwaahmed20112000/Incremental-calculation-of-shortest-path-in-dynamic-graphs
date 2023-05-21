package org.example;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;


public class Client {

    private Client() {}

    /**
     * Performance Analysis:
     * Response Time(median of 20 trials) vs Frequency of Requests(multiple of ten quires)
     * @param stub for RMI processing
     */
    private static void responseTimeVsRequestsFrequency(boolean optimized, String clientID, BatchProcessing stub)
            throws IOException, InterruptedException {

        Logger logger = new Logger("Client" + clientID + "/responseTimeVsRequestsFrequency.txt");
        int quiresNum = 0, trials = 5, points = 10, percent = 50;
        for (int j = 0; j < points; j++) {
            long[] median = new long[trials];
            quiresNum += 200;
            for (int i = 0; i < trials; i++) {
                String path = "Client" + clientID + "/responseTimeVsRequestsFrequency/batch_"
                        + j + "_Trial_" + i + ".txt";
                String batch;

                if(optimized) {
                    batch = new String(Files.readAllBytes(Paths.get(path)));
                    System.out.println("Opt");
                }
                else batch = Utils.generateBiasedBatch(path, quiresNum, percent);

                // Calculate Response Time
                long start = System.currentTimeMillis();
                List<Integer> results = stub.processBatch(clientID, batch);
                long end = System.currentTimeMillis();

                median[i] = (end - start);

                // Sleep till the next batch
                Thread.sleep(ThreadLocalRandom.current().nextInt(1, 11)* 1000L);
            }
            Arrays.sort(median);
            logger.log("Point " + (j+1) + ": Response Time = " + median[trials/2] + " ms");
        }
    }


    /**
     * Performance Analysis:
     * Response Time(median of 20 trials) vs Percentage of Add/Delete Operations(multiple of tenth)
     * Quires = 30 query
     * @param stub for RMI processing
     */
    private static void responseTimeVsOperationsPercentage(boolean optimized, String clientID, BatchProcessing stub)
            throws IOException, InterruptedException {

        Logger logger = new Logger("Client" + clientID + "/responseTimeVsOperationsPercentage.txt");

        int percent = 10, trials = 5, points = 10, quires = 1000;
        for (int j = 0; j < points; j++) {

            long[] median = new long[trials];
            for (int i = 0; i < trials; i++) {

                String path = "Client" + clientID + "/responseTimeVsOperationsPercentage/batch_"
                        + j + "_Trial_" + i + ".txt";
                String batch;
                if(optimized) batch = new String(Files.readAllBytes(Paths.get(path)));
                else batch = Utils.generateBiasedBatch(path, quires, percent*(j+1));

                // Calculate Response Time
                long start = System.currentTimeMillis();
                List<Integer> results = stub.processBatch(clientID, batch);
                long end = System.currentTimeMillis();

                median[i] = (end - start);

                // Sleep till the next batch
                Thread.sleep(ThreadLocalRandom.current().nextInt(1, 11)* 1000L);
            }
            Arrays.sort(median);
            logger.log("Point " + (j+1) + ": Response Time = " + median[trials/2] + " ms");
        }
    }

    /**
     * Performance Analysis:
     * Response Time(median of 20 trials) vs Number of Nodes(1:5)
     * Quires = 30 query
     * @param stub for RMI processing
     */
    private static void responseTimeVsNumberOfNodes(boolean optimized, String clientID, BatchProcessing stub)
            throws IOException, InterruptedException {

        Logger logger = new Logger("Client" + clientID + "/responseTimeVsNumberOfNodes13");
        String path = "Client" + clientID + "/responseTimeVsNumberOfNodes13/batch_";

        int trials = 5, quires = 1000, percent = 50;
        long[] median = new long[trials];
        for (int i = 0; i < trials; i++) {
            String batch;
            if(optimized) batch = new String(Files.readAllBytes(Paths.get(path + i + ".txt")));
            else batch = Utils.generateBiasedBatch(path + i + ".txt", quires, percent);

            // Calculate Response Time
            long start = System.currentTimeMillis();
            List<Integer> results = stub.processBatch(clientID, batch);
            long end = System.currentTimeMillis();

            median[i] = (end - start);

            // Sleep till the next batch
            Thread.sleep(ThreadLocalRandom.current().nextInt(1, 11)* 1000L);
        }
        Arrays.sort(median);
        logger.log("Response Time = " + median[trials/2] + " ms");
    }


    public static void main(String[] args) {

        try {
            // Connect to RMI Registry
            Registry registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
            BatchProcessing stub = (BatchProcessing) registry.lookup("BatchProcessing");

            if(args[3].compareTo("0") == 0)
                responseTimeVsRequestsFrequency(Objects.equals(args[4], "1"), args[2], stub);
            else if(args[3].compareTo("1") == 0)
                responseTimeVsOperationsPercentage(Objects.equals(args[4], "1"), args[2], stub);
            else if(args[3].compareTo("2") == 0)
                responseTimeVsNumberOfNodes(Objects.equals(args[4], "1"), args[2], stub);
            else if(args[3].compareTo("3") == 0) {
                String batch;
                if(Objects.equals(args[4], "1"))
                    batch = new String(Files.readAllBytes(Paths.get("Input.txt")));
                else
                    batch = Utils.generateBiasedBatch("Client" + args[2] +
                            "/batch.txt" , Integer.parseInt(args[5]), 50);

                System.out.println("Batch");

                long start = System.currentTimeMillis();
                List<Integer> results = stub.processBatch(args[2], batch);
                long end = System.currentTimeMillis();

                Logger logger = new Logger("Client" + args[2] + "/output");
                for (Integer result : results) logger.log(String.valueOf(result));
                logger.log("Response Time = " + (end - start) + " ms");
            }

        } catch (Exception e) {
            System.err.println("Client exception: " + e);
            e.printStackTrace();
        }
    }
}