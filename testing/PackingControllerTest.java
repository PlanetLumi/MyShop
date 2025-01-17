package testing;

import clients.packing.PackingController;
import clients.packing.PackingModel;
import org.junit.jupiter.api.Test;
import javax.swing.*;
import static org.junit.Assert.assertNull;

class PackingControllerTest {
    void testDoPacked() {
        PackingModel model = new PackingModel(new InMemoryStockReadWriter(), new InMemoryOrderProcessing());
        PackingController controller = new PackingController(model);

        controller.doPacked();

        // Validate that the model's state is as expected
        assertNull(model.getBasket().get());
    }
}
