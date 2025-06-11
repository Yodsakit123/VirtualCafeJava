import java.io.*;
import java.net.*;
import java.util.Scanner;

// Customer client implementation
public class Customer {
    //Server address and server port, make sure the port match Barista server port and the server adress is localhost
    private static final String SERVER_ADDRESS = "127.0.0.1";
    private static final int SERVER_PORT = 12345;

    //main method
    public static void main(String[] args) {
        //Socket use to connect to the server
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        //read message from terminal and send message to terminal
             DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        //Scanner
             Scanner scanner = new Scanner(System.in)) {

            System.out.println("Connected to the cafe!");
            System.out.println(input.readUTF()); // Welcome message
            String clientName = scanner.nextLine();
            output.writeUTF(clientName);

            String serverResponse;
            while (true) {
                //check if user types message in the server
                if (input.available() > 0) {
                    //read
                    serverResponse = input.readUTF();
                    //print
                    System.out.println("Server: " + serverResponse);
                    //check if user types exit
                    if (serverResponse.startsWith("Goodbye")) {
                        break;
                    }
                }
                //recheck if user types command 
                if (System.in.available() > 0) {
                    //read
                    String command = scanner.nextLine();
                    //print
                    output.writeUTF(command);
                }
            }

        } catch (IOException e) {
            System.out.println("Disconnected from the server.");
        }
    }
}

