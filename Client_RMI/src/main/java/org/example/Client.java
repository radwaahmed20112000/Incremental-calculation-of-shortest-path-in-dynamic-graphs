package org.example;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
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
    private static void responseTimeVsRequestsFrequency(BatchProcessing stub) throws RemoteException,
            InterruptedException {

        int quiresNum = 10, trials = 20, points = 10;
        for (int j = 0; j < points; j++) {

            long[] median = new long[trials];
            for (int i = 0; i < trials; i++) {

                String batch = Utils.generateBatch(quiresNum*(j+1), i);
                // Calculate Response Time
                long start = System.currentTimeMillis();
                List<Integer> results = stub.processBatch(batch);
                long end = System.currentTimeMillis();

                median[i] = (end - start);
                // Print Output
                for (int result : results)
                    System.out.println(result);

                // Sleep till the next batch
                Thread.sleep(ThreadLocalRandom.current().nextInt(1, 11)* 1000L);
            }
            Arrays.sort(median);
            System.out.println("Trial " + (j+1) + ": Response Time = " + median[trials/2] + " ms");
        }
    }


    /**
     * Performance Analysis:
     * Response Time(median of 20 trials) vs Percentage of Add/Delete Operations(multiple of tenth)
     * Quires = 30 query
     * @param stub for RMI processing
     */
    private static void responseTimeVsOperationsPercentage(BatchProcessing stub) throws RemoteException,
            InterruptedException {

        int percent = 10, trials = 20, points = 10, quires = 30;
        for (int j = 0; j < points; j++) {

            long[] median = new long[trials];
            for (int i = 0; i < trials; i++) {

                String batch = Utils.generateBiasedBatch(quires, i, percent*(j+1));
                System.out.println(batch);
                // Calculate Response Time
                long start = System.currentTimeMillis();
                List<Integer> results = stub.processBatch(batch);
                long end = System.currentTimeMillis();

                median[i] = (end - start);
                // Print Output
                for (int result : results)
                    System.out.println(result);

                // Sleep till the next batch
                Thread.sleep(ThreadLocalRandom.current().nextInt(1, 11)* 1000L);
            }
            Arrays.sort(median);
            System.out.println("Trial " + (j+1) + ": Response Time = " + median[trials/2] + " ms");
        }
    }

    /**
     * Performance Analysis:
     * Response Time(median of 20 trials) vs Number of Nodes(1:5)
     * Quires = 30 query
     * @param stub for RMI processing
     */
    private static void responseTimeVsNumberOfNodes(BatchProcessing stub) throws RemoteException, InterruptedException {

        int trials = 20, quires = 30;
        long[] median = new long[trials];
        for (int i = 0; i < trials; i++) {

            String batch = Utils.generateBatch(quires, i);
            // Calculate Response Time
            long start = System.currentTimeMillis();
            List<Integer> results = stub.processBatch(batch);
            long end = System.currentTimeMillis();

            median[i] = (end - start);
            // Print Output
            for (int result : results)
                System.out.println(result);

            // Sleep till the next batch
            Thread.sleep(ThreadLocalRandom.current().nextInt(1, 11)* 1000L);
        }
        Arrays.sort(median);
        System.out.println("Response Time = " + median[trials/2] + " ms");
    }


    public static void main(String[] args) {

        try {
            // Connect to RMI Registry
            Registry registry = LocateRegistry.getRegistry(args[0], Integer.parseInt(args[1]));
            BatchProcessing stub = (BatchProcessing) registry.lookup("BatchProcessing");

//            responseTimeVsRequestsFrequency(stub);
            responseTimeVsOperationsPercentage(stub);
//            responseTimeVsNumberOfNodes(stub);

        } catch (Exception e) {
            System.err.println("Client exception: " + e);
            e.printStackTrace();
        }
    }
}