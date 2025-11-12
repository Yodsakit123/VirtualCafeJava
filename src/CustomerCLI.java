import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Scanner;

public class CustomerCLI {
    private static final Type ORDER_LIST = new TypeToken<List<Order>>(){}.getType();

    private static void listOrders(ClientApi api) throws Exception {
        Message resp = api.send(new Message("LIST_ORDERS", "{}"));
        if ("OK".equals(resp.action)) {
            List<Order> orders = DataManager.gson().fromJson(resp.payload, ORDER_LIST);
            System.out.println("=== ORDERS ===");
            if (orders.isEmpty()) {
                System.out.println("No orders yet.");
            } else {
                for (Order o : orders) {
                    System.out.printf("#%d %-12s x%-2d %-12s £%.2f [%s]%n",
                            o.id, o.item, o.quantity, o.customerName, o.total(), o.status);
                }
            }
        } else {
            System.out.println("Error: " + resp.payload);
        }
    }

    private static void placeOrder(ClientApi api, Scanner sc) throws Exception {
        System.out.print("Customer name: ");
        String name = sc.nextLine();
        System.out.print("Item: ");
        String item = sc.nextLine();
        System.out.print("Quantity: ");
        int qty = Integer.parseInt(sc.nextLine());
        System.out.print("Price each: ");
        double price = Double.parseDouble(sc.nextLine());

        // Use JsonObject for proper JSON formatting
        JsonObject json = new JsonObject();
        json.addProperty("customerName", name);
        json.addProperty("item", item);
        json.addProperty("quantity", qty);
        json.addProperty("priceEach", price);
        String payload = json.toString();

        Message resp = api.send(new Message("PLACE_ORDER", payload));
        if ("CREATED".equals(resp.action)) {
            Order order = DataManager.gson().fromJson(resp.payload, Order.class);
            System.out.println("✓ Order created: #" + order.id);
        } else {
            System.out.println("Error: " + resp.payload);
        }
    }

    private static void updateStatus(ClientApi api, Scanner sc) throws Exception {
        System.out.print("Order ID: ");
        int id = Integer.parseInt(sc.nextLine());
        System.out.print("New status (PENDING/IN_PROGRESS/SERVED/CANCELLED): ");
        String status = sc.nextLine().toUpperCase();
        
        // Use JsonObject for proper JSON formatting
        JsonObject json = new JsonObject();
        json.addProperty("id", id);
        json.addProperty("status", status);
        String payload = json.toString();

        Message resp = api.send(new Message("UPDATE_STATUS", payload));
        if ("OK".equals(resp.action)) {
            System.out.println("✓ Status updated");
        } else {
            System.out.println("Error: " + resp.payload);
        }
    }

    public static void main(String[] args) throws Exception {
        String host = (args.length > 0) ? args[0] : "127.0.0.1";
        int port = (args.length > 1) ? Integer.parseInt(args[1]) : 5050;

        try (ClientApi api = new ClientApi(host, port);
             Scanner sc = new Scanner(System.in)) {
            System.out.println("=== Cafe Order System CLI ===");
            while (true) {
                System.out.println("\n1) List orders  2) Place order  3) Update status  0) Exit");
                System.out.print("> ");
                String choice = sc.nextLine().trim();
                try {
                    switch (choice) {
                        case "1": listOrders(api); break;
                        case "2": placeOrder(api, sc); break;
                        case "3": updateStatus(api, sc); break;
                        case "0": 
                            System.out.println("Goodbye!");
                            return;
                        default: System.out.println("Unknown option.");
                    }
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        }
    }
}