package testing;

import clients.accounts.ProductDB;
import clients.packing.PackingController;
import clients.packing.PackingModel;
import middle.MiddleFactory;
import middle.OrderProcessing;
import middle.StockReadWriter;
import org.junit.jupiter.api.*;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PackingModelTest {
    @Test
    void testDoPacked() {
        // Use the in-memory fakes
        InMemoryStockReadWriter stock = new InMemoryStockReadWriter();
        InMemoryOrderProcessing order = new InMemoryOrderProcessing();

        PackingModel model = new PackingModel(stock, order);
        PackingController controller = new PackingController(model);

        controller.doPacked();

        // Validate that the model's state is as expected
        assertNull(model.getBasket().get());
    }
}