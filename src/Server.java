import java.net.*;
import java.util.HashSet;
import java.util.Random;
import java.io.*;

public class Server {

    public static Random r = new Random();


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
        HashSet<Integer> nums = new HashSet<>();
        fillHashSet(nums, messages);
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
            int sentAmount = 0;
            while ((usersRequest = requestReader1.readLine()) != null) {
                sentAmount = pickSendingNumber(nums);
                String baseString = messages[sentAmount];
                String protocol = "/" + sentAmount + "-" + messages.length;
                String sendingString = baseString + protocol;
                int ifToSend = r.nextInt(0, 9);
                    if (ifToSend < 8){
                        responseWriter1.println(sendingString);
                    }
                }
                responseWriter1.println("Done");
            } catch (IOException e) {
                System.out.println(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
                System.out.println(e.getMessage());
            }

    }

    private static int pickSendingNumber(HashSet<Integer> nums) {
        int i = 0;
        int send = 0;
        int sendIndex = r.nextInt(0, nums.size());
        for (int element : nums) {
            if (i == sendIndex) {
                send =  element;
            }
            i++;
        }
        nums.remove(send);
        return send;
    }

    private static void fillHashSet(HashSet<Integer> nums, String[] messages) {
        for (int i = 0; i < messages.length; i++) {
            nums.add(i);
        }
    }

}
