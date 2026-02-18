import java.net.*;
import java.util.Random;
import java.io.*;

public class Server {



    public static void main(String[] args) throws IOException {
        String[] messages = {"Mine eyes", "have seen",
                "the coming", "of the", "glory of",
                "the lord", "He is", "trampling on",
                "the vintage", "where the", "grapes of",
                "wrath are", "stored. He", "hath loosed",
                "the fateful", "lightning of", "his terrible",
                "swift sword", "his truth", "is marching on",
                "glory, glory,", "Hallelujah", "Battle Hymn",
                "of The Republic."
        };
        Random r = new Random();
        int[] sent = new int[messages.length];
        // Hard code in port number if necessary:
        //args = new String[] { "30121" };

        if (args.length != 1) {
            System.err.println("Usage: java EchoServer <port number>");
            System.exit(1);
        }

        int portNumber = Integer.parseInt(args[0]);

        try (ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
             Socket clientSocket1 = serverSocket.accept();
             PrintWriter responseWriter1 = new PrintWriter(clientSocket1.getOutputStream(), true);
             BufferedReader requestReader1 = new BufferedReader(new InputStreamReader(clientSocket1.getInputStream()));
        ) {
            String usersRequest;
            while ((usersRequest = requestReader1.readLine()) != null) {
                for (int i = 0; i < messages.length; i++) {
                    int sendingNumber = 0;
                    while (sent[sendingNumber] != 1) {
                        sendingNumber = r.nextInt(0, messages.length);
                        sent[sendingNumber] = 1;
                    }
                    String baseString = messages[sendingNumber];
                    String protocol = "-" + sendingNumber + "-" + messages.length;
                    String sendingString = baseString + protocol;
                    int ifToSend = r.nextInt(0, 9);
                    if (ifToSend < 8){
                        responseWriter1.println(sendingString);
                    }
                }
                responseWriter1.println("Done");
            }
        } catch (IOException e) {
            System.out.println(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }

    }

}
