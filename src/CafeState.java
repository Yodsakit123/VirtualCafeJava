import java.util.ArrayList;
import java.util.List;

public class CafeState {
    public List<Order> orders = new ArrayList<>();
    public int nextOrderId = 1;

    public synchronized int nextId() {
        return nextOrderId++;
    }

    public synchronized Order addOrder(String customer, String item, int qty, double priceEach) {
        Order o = new Order(nextId(), customer, item, qty, priceEach, "PENDING");
        orders.add(o);
        return o;
    }

    public synchronized Order find(int id) {
        for (Order o : orders) if (o.id == id) return o;
        return null;
    }

    public synchronized boolean updateStatus(int id, String status) {
        Order o = find(id);
        if (o == null) return false;
        o.status = status;
        return true;
    }
}

