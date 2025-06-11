//order class
class Order {
    private int teaCount = 0;
    private int coffeeCount = 0;

    public synchronized void addTea(int count) {    
        teaCount += count;   
    }

    public synchronized void addCoffee(int count) {
        coffeeCount += count;
    }

    public synchronized int getTeaCount() {
        return teaCount;
    }

    public synchronized int getCoffeeCount() {
        return coffeeCount;
    }

    public synchronized String getOrderSummary() {
        return teaCount + " teas and " + coffeeCount + " coffees.";
    }
}
