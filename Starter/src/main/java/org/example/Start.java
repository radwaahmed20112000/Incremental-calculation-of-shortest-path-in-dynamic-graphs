package org.example;


import java.io.*;
import java.util.Properties;

public class Start {
    public static void main(String[] args) throws IOException {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("/home/radwa/Documents/College/DistributedSystems/Incremental-" +
                    "calculation-of-shortest-path-in-dynamic-graphs/Starter/src/main/java/org/example/system.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the values of the properties
        String server = props.getProperty("GSP.server");
        String rmiPort = props.getProperty("GSP.rmiregistry.port");

        String[] commandArray = { "/bin/bash", "-c",
                        "java -jar /home/radwa/Documents/College/DistributedSystems/Incremental-calculation-of-shortest" +
                                "-path-in-dynamic-graphs/Starter/src/main/java/org/example/Processes/Server_RMI.jar " +
                                server + " " + rmiPort + ";"};
        Process process = Runtime.getRuntime().exec(commandArray);

        // Read the output of the process
        InputStream inputStream = process.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }

        int clientsNumber = Integer.parseInt(props.getProperty("GSP.numberOfnodes"));

        String[] clients = new String[clientsNumber];
        for (int i = 0; i < clientsNumber; i++) {
            clients[i] = props.getProperty("GSP.node" + i);
            System.out.println("Client " + i + ": " + clients[i]);

            commandArray = new String[]{"/bin/bash", "-c",
                    "java -jar /home/radwa/Documents/College/DistributedSystems/Incremental-calculation-of-shortest" +
                            "-path-in-dynamic-graphs/Starter/src/main/java/org/example/Processes/Client_RMI.jar " +
                            server + " " + rmiPort + ";"};

            Runtime.getRuntime().exec(commandArray);
        }
    }
}