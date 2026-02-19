import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Client {
    public static void main(String[] args) throws IOException {

        // Hardcode in IP and Port here if required
        //args = new String[] {"127.0.0.1", "30121"};

        if (args.length != 2) {
            System.err.println(
                    "Usage: java EchoClient <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int portNumber = Integer.parseInt(args[1]);

        try (
                Socket clientSocket = new Socket(hostName, portNumber);
                PrintWriter requestWriter = // stream to write text requests to server
                        new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader responseReader= // stream to read text response from server
                        new BufferedReader(
                                new InputStreamReader(clientSocket.getInputStream()));
                BufferedReader stdIn = // standard input stream to get user's requests
                        new BufferedReader(
                                new InputStreamReader(System.in))
        ) {
            String userInput;
            String serverResponse;
            String currentString = "";
            String totalString;
            HashMap<Integer, String> packets = new HashMap<>();
            int totalInt = 0;
            int currentInt = 0;
            HashSet<Integer> missingPackets = new HashSet<>();
            boolean doneReceived = false;
            while ((userInput = stdIn.readLine()) != null) {
                requestWriter.println(userInput); // send request to server
                while ((serverResponse = responseReader.readLine()) != null) {
                    if (serverResponse.equals("Done")) {
                        doneReceived = true;
                    }
                    processLine(serverResponse, missingPackets, packets);
                }
                while (!missingPackets.isEmpty()) {
                    String missing = "";
                    for (int i : missingPackets) {
                        missing += i;
                        missing += ",";
                    }
                    requestWriter.println(missing);
                }
            System.out.println("SERVER RESPONDS: \"" + serverResponse + "\"");

            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostName);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " +
                    hostName);
            System.exit(1);
        }
    }

    private static void processLine(String serverResponse, HashSet<Integer> missingPackets, HashMap<Integer, String> packets) {
        int currentInt;
        String totalString;
        String currentString;
        int totalInt;
        for (int i = serverResponse.length() - 1; i >= 0; i--) {
            if (serverResponse.charAt(i) == '-'){
                totalString = serverResponse.substring(i + 1);
                serverResponse = serverResponse.substring(0,i);
                totalInt = Integer.parseInt(totalString);
                if (missingPackets.isEmpty()){
                    for (int j = 0; j < totalInt; j++) {
                        missingPackets.add(j);
                    }
                }
            }
            if (serverResponse.charAt(i) == '-'){
                currentString = serverResponse.substring(i + 1);
                serverResponse = serverResponse.substring(0,i);
                currentInt = Integer.parseInt(currentString);
                missingPackets.remove(currentInt);
                packets.put(currentInt, serverResponse);
                break;
            }
        }
    }
}
