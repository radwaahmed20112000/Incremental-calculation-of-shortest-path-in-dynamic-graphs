package org.example;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Start {
    public static void main(String[] args) throws IOException, InterruptedException {

        Properties props = new Properties();
        try {
            props.load(new FileInputStream("./src/main/java/org/example/system.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Get the values of the properties
        String server = props.getProperty("GSP.server");
        String rmiPort = props.getProperty("GSP.rmiregistry.port");

        // Arguments: <server-ip> <port>
        // Server_RMI_1/0.jav -> Optimized or Not
        String[] commandArr = { "/bin/bash", "-c",
                "java -jar ./src/main/java/org/example/Processes/Server_RMI_0.jar " +
                        server + " " + rmiPort + ";"};
        Runtime.getRuntime().exec(commandArr);

        ExecutorService executor = Executors.newFixedThreadPool(Integer.parseInt(props.
                getProperty("GSP.numberOfnodes")));

        // Arguments: <server-ip> <port> <client-id> <service-type> <input-type:file or random> <batch-size>
        for (int i = 0; i < Integer.parseInt(props.getProperty("GSP.numberOfnodes")); i++) {
            String node = props.getProperty("GSP.node" + i);
            System.out.println("Client " + i + ": " + node);

            String[] commandArray = { "/bin/bash", "-c",
                    "java -jar ./src/main/java/org/example/Processes/Client_RMI.jar " +
                            server + " " + rmiPort + " " + i + " 3 0 1000;"};

            Process process = Runtime.getRuntime().exec(commandArray);

            // Submit the process as a task to the executor
            executor.submit(() -> {
                try {
                    // Read the output of the process
                    InputStream inputStream = process.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                    // Wait for the process to complete
                    process.waitFor();
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        // Shutdown the executor once all tasks have been submitted
        executor.shutdown();

        // Wait for all tasks to complete
        while (!executor.isTerminated()) {
            Thread.sleep(1000);
        }

        // Exit the program
        System.exit(0);
    }
}