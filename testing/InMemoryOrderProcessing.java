package testing;

import middle.OrderProcessing;
import middle.OrderException;
import catalogue.Basket;

import java.util.List;
import java.util.Map;

public class InMemoryOrderProcessing implements OrderProcessing {
    @Override
    public void newOrder(Basket bought) throws OrderException {

    }

    @Override
    public int uniqueNumber() throws OrderException {
        return 0;
    }

    @Override
    public Basket getOrderToPack() throws OrderException {
        return null; // Return a fake Basket or null as needed
    }

    @Override
    public boolean informOrderPacked(int orderNo) throws OrderException {
        // Simulate behavior for testing
        return false;
    }

    @Override
    public boolean informOrderCollected(int orderNum) throws OrderException {
        return false;
    }

    @Override
    public Map<String, List<Integer>> getOrderState() throws OrderException {
        return Map.of();
    }

    // Add other required methods
}