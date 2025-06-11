import java.io.*;
import java.net.*;
import java.util.concurrent.*;

// Barista server implementation
public class Barista {
    //Barista server port and thread pool to handle multiple connections
    private static final int PORT = 12345;
    private static final ExecutorService pool = Executors.newCachedThreadPool();
    private static final CafeState cafeState = new CafeState();
    //main method
    public static void main(String[] args) {
        System.out.println("Barista server is starting...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                pool.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pool.shutdown();
        }
    }
//ClientHandler class
    static class ClientHandler implements Runnable {
        private final Socket socket;
        private String clientName;
        private final DataInputStream input;
        private final DataOutputStream output;

        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket;
            this.input = new DataInputStream(socket.getInputStream());
            this.output = new DataOutputStream(socket.getOutputStream());
        }

        @Override
        public void run() {
            try {
                output.writeUTF("Welcome to the cafe! Please provide your name:");
                clientName = input.readUTF();
                System.out.println(clientName + " has joined the cafe");
                output.writeUTF("Hello, " + clientName + " Enter your order (e.g. 1 tea, 2 coffees). 'order status' to check your order, 'collect' to pick up, or 'exit' to leave. 'help' to repeat the command");
                
                String message;
                while (!(message = input.readUTF()).equalsIgnoreCase("exit")) {
                    System.out.println(clientName + " sent: " + message);
                    String response;
                    
                    //user's command and response
                    if (message.equalsIgnoreCase("order status")) {
                        response = cafeState.getOrderStatus(clientName);
                    } else if (message.equalsIgnoreCase("collect")) {
                        response = cafeState.collectOrder(clientName);
                    } else if (message.equalsIgnoreCase("help")){
                        response = cafeState.helpmessage();
                    }
                     else {
                        response = cafeState.addOrder(clientName, message);
                    }

                    output.writeUTF(response);
                }
                //print Goodbye if user types exit
                output.writeUTF("Goodbye, " + clientName + "!");
                System.out.println(clientName + " has left the cafe");
            } catch (IOException e) {
                System.out.println(clientName + " disconnected.");
            } finally {
                try {
                    cafeState.removeClient(clientName);
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}