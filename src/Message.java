public class Message {
    public String action;   // e.g., "LIST_ORDERS", "PLACE_ORDER", "UPDATE_STATUS", "PING"
    public String payload;  // JSON string for the action-specific data (kept simple)

    public Message() {}
    public Message(String action, String payload) {
        this.action = action;
        this.payload = payload;
    }
}
