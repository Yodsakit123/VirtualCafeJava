import java.time.Instant;

public class Order {
    public int id;
    public String customerName;
    public String item;
    public int quantity;
    public double priceEach;
    public String status; // "PENDING", "IN_PROGRESS", "SERVED", "CANCELLED"
    public Instant createdAt;

    public Order() {}

    public Order(int id, String customerName, String item, int quantity, double priceEach, String status) {
        this.id = id;
        this.customerName = customerName;
        this.item = item;
        this.quantity = quantity;
        this.priceEach = priceEach;
        this.status = status;
        this.createdAt = Instant.now();
    }

    public double total() {
        return quantity * priceEach;
    }
}
