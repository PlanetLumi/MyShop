package testing;

import dbAccess.DBAccess;
import dbAccess.DBAccessFactory;

import java.util.List;
import java.util.Map;

public class FakeProductDB extends DBAccess {

    public List<Map<String, Object>> fetchUnpackedOrdersFromDB() {
        return List.of(
                Map.of("orderId", 1, "status", "unpacked"),
                Map.of("orderId", 2, "status", "unpacked")
        );
    }

    public void setOrderStatus(Long orderId, String status) {

    }
}
