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
                BufferedReader responseReader = // stream to read text response from server
                        new BufferedReader(
                                new InputStreamReader(clientSocket.getInputStream()));
                BufferedReader stdIn = // standard input stream to get user's requests
                        new BufferedReader(
                                new InputStreamReader(System.in))
        ) {
            String userInput;
            String serverResponse;
            HashMap<Integer, String> packets = new HashMap<>(); // This will hold the packets it receives.
            HashSet<Integer> missingPackets = new HashSet<>(); // This will hold the number of the packets it did not yet receive.
            userInput = stdIn.readLine();
            while (userInput != null) {
                userInput += ";"; // So that the server can differentiate between a regular request and a request to resend packets.
                requestWriter.println(userInput);// send request to server
                do {
                    getServerResponse(responseReader, missingPackets, packets);
                    if (!missingPackets.isEmpty()) { // We have to send a request to get the missing packets.
                        String missing = "";
                        for (int i : missingPackets) {
                            missing += i;
                            missing += " ";
                        }
                        requestWriter.println(missing); // Sends a s tring with the packet numbers we do not have.
                    } else {
                        break;
                    }
                } while (!missingPackets.isEmpty()); // Loop this while we still need packets.
                String text = processText(packets); // Rearrange the packets, once we have them all.
                System.out.println(text);
                userInput = stdIn.readLine();

            }
            } catch(UnknownHostException e){
                System.err.println("Don't know about host " + hostName);
                System.exit(1);
            } catch(IOException e){
                System.err.println("Couldn't get I/O for the connection to " +
                        hostName);
                System.exit(1);
            }
        }

    private static void getServerResponse(BufferedReader responseReader, HashSet<Integer> missingPackets, HashMap<Integer, String> packets) throws IOException {
        String serverResponse;
        while ((serverResponse = responseReader.readLine()) != null) {
            if (serverResponse.equals("Done")) {
                break;
            }
            processLine(serverResponse, missingPackets, packets);

        }
    }

    private static String processText(HashMap<Integer, String> packets) {
        String text = "";
        for (int i = 0; i < packets.size(); i++) {
            text += packets.get(i); // Add to the string to be output the packets, ordered by packet number.
        }
        return text;
    }


    private static void processLine(String serverResponse, HashSet<Integer> missingPackets, HashMap<Integer, String> packets) {
        int currentInt;
        String totalString;
        String currentString;
        int totalInt;
        for (int i = serverResponse.length() - 1; i >= 0; i--) {
            if (serverResponse.charAt(i) == '-'){ // The numbers after this will always be the total number of packets.
                totalString = serverResponse.substring(i + 1);
                serverResponse = serverResponse.substring(0,i);
                totalInt = Integer.parseInt(totalString);
                if (missingPackets.isEmpty()){
                    for (int j = 0; j < totalInt; j++) {
                        missingPackets.add(j); // Initialize the missing packets to be full, because, so far, we have not processed a single packet.
                    }
                }
                continue;
            }
            if (serverResponse.charAt(i) == '/'){// The numbers after this will always be the current number of packets.
                currentString = serverResponse.substring(i + 1);
                serverResponse = serverResponse.substring(0,i);
                currentInt = Integer.parseInt(currentString);
                missingPackets.remove(currentInt); // The packet is no longer missing.
                packets.put(currentInt, serverResponse); // Put the received packet in our hashmap of packets.
                break;
            }

        }

    }
}
