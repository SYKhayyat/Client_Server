import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;

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
            HashMap<Integer, String> packets = new HashMap<>();
            String part1, part2, part3;
            part3 = null;
            boolean received;
            boolean part1Gotten = false;
            int total = 0;
            while ((userInput = stdIn.readLine()) != null) {
                requestWriter.println(userInput); // send request to server
                while((serverResponse = responseReader.readLine()) != null){
                    for (int i = serverResponse.length() - 1; i <= 0; i++) {
                        part1Gotten = false;
                        if (serverResponse.charAt(i) == '-' && !part1Gotten){
                            part3 = serverResponse.substring(i + 1, serverResponse.length());
                            part1Gotten = true;
                            serverResponse = serverResponse.substring(0,i);
                        }
                        if (serverResponse.charAt(i) == '-' && part1Gotten){
                            part2 = serverResponse.substring(i + 1, serverResponse.length());
                            serverResponse = serverResponse.substring(0,i);
                            break;
                        }
                    }
                    if (part1Gotten){
                        packets.put(Integer.parseInt(part2), serverResponse);
                    }
                }
                if (!part1Gotten){
                    String done = serverResponse;
                }
                if (part3 != null){
                    total = Integer.parseInt(part3);
                    received = true;
                } else {
                    received = false;
                }
                ArrayList<Integer> missing = new ArrayList<>();
                if (received){
                    for (int i = 0; i < total; i++) {
                        if (! packets.containsKey(i)) {
                            missing.add(i);
                        }
                    }
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
}
