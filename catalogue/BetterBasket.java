package catalogue;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.Optional;

/**
 * Write a description of class BetterBasket here.
 *
 * @author Seth Humphries
 * @version 1.0
 */
public class BetterBasket extends Basket implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Add a product to the basket.
     * Unlike {@link Basket}, multiple instances with the same product number will be merged into a single
     * {@link Product} of appropriate quantity.
     * @param product element whose presence in this collection is to be ensured.
     * @return true if the product was added successfully.
     */
    @Override
    public boolean add(Product product) {
        // Look for another Product in the basket with the same product number.
        Optional<Product> duplicate = this.stream()
                .filter((productInBasket) -> productInBasket.getProductNum().equals(product.getProductNum()))
                .findAny();

        if (duplicate.isPresent()) {
            Product productInBasket = duplicate.get();
            int newQuantity = productInBasket.getQuantity() + product.getQuantity();
            productInBasket.setQuantity(newQuantity);
            return true; // Duplicates were merged, so return early.
        }

        return super.add(product); // Add the product to this list normally.
    }

    // You need to add code here
    // merge the items for same product,
    // or sort the item based on the product number
}
