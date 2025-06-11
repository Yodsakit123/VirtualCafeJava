import java.util.concurrent.*;
//CafeState class
class CafeState {
    private final ConcurrentHashMap<String, Order> clientOrders = new ConcurrentHashMap<>();
    
    //Add order
    public synchronized String addOrder(String clientName, String orderDetails) {
        clientOrders.putIfAbsent(clientName, new Order());
        Order order = clientOrders.get(clientName);

        String[] items = orderDetails.split(",\s*");
        for (String item : items) {
            if (item.contains("tea")) {
                int quantity = extractQuantity(item);
                if (quantity < 0){
                    return "Order can not be negative" + helpmessage();
                }
                else{
                order.addTea(quantity);
            }
            } else if (item.contains("coffee")) {
                int quantity = extractQuantity(item);
                if (quantity < 0){
                    return "Order can not be negative" + helpmessage();
                }
                else{
                order.addCoffee(quantity);
                }
            }
        }

        return "Order received: " + order.getOrderSummary();
    }
    //Get order status
    public synchronized String getOrderStatus(String clientName) {
        Order order = clientOrders.get(clientName);
        if (order == null || (order.getTeaCount() == 0 && order.getCoffeeCount() == 0)) {
            return "No order found for " + clientName;
        }

        return "Order status: " + order.getOrderSummary();
    }
    //Collect order
    public synchronized String collectOrder(String clientName) {
        Order order = clientOrders.remove(clientName);
        if (order == null || (order.getTeaCount() == 0 && order.getCoffeeCount() == 0)) {
            return "No order ready for " + clientName;
        }

        return "Order collected: " + order.getOrderSummary();
    }
    //repeat message
    public synchronized String helpmessage(){
        return (" Enter your order (e.g. 1 tea, 2 coffees). 'order status' to check your order, 'collect' to pick up, or 'exit' to leave 'help' to repeat the command");
    }
    //remove user when disconnect
    public synchronized void removeClient(String clientName) {
        clientOrders.remove(clientName);
    }
    
    private int extractQuantity(String item) {
        String[] parts = item.split(" ");
        try {
            return Integer.parseInt(parts[0]);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            return 1; // Default to 1 if no valid quantity is provided
        }
    }
}