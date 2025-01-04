package clients.productlist;

import catalogue.Product;
import debug.DEBUG;
import middle.MiddleFactory;
import middle.StockException;
import middle.StockReader;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements the Model for the Products List Client.
 */
public class ProductsListModel {
    private final List<Product> productList = new ArrayList<>(); // List of products
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);
    private StockReader theStock = null;

    /**
     * Constructor for the Products List Model.
     *
     * @param mf MiddleFactory instance
     */
    public ProductsListModel(MiddleFactory mf) {
        try {
            theStock = mf.makeStockReader(); // Initialize StockReader from middle layer
        } catch (Exception e) {
            DEBUG.error("ProductsListModel.constructor\nDatabase not created? %s", e.getMessage());
        }
    }

    /**
     * Load the list of all products from the database.
     */
    public void loadProducts() {
        try {
            List<Product> oldProductList = new ArrayList<>(productList);
            productList.clear(); // Clear the current list
            System.out.println("loading products");
            DEBUG.trace("ProductsListModel.loadProducts()\nLoading products");

            for (int productNo = 1; ; productNo++) { // Assuming product IDs start from 1 and are sequential
                String productID = String.format("%04d", productNo); // Format product numbers as "001", "002", etc.
                System.out.printf("checking product %s\n", productID);
                if (!theStock.exists(productID)) { // Stop trying when no more products exist
                    DEBUG.trace("ProductsListModel.loadProducts()\nNo more products found");
                    break;
                }
                // if a product exists, print its product number in the console
                DEBUG.trace("ProductsListModel.loadProducts()\nProduct %s exists", productID);
                Product product = theStock.getDetails(productID); // Fetch product details
                if (product == null) {
                    DEBUG.error("ProductsListModel.loadProducts()\nProduct %s not found", productID);
                    System.out.println("Product not found");
                    continue;
                }

                productList.add(product); // Add product to list
            }

            if (productList.isEmpty()) {
                DEBUG.trace("ProductsListModel.loadProducts()\nNo products found");
                
            }

            support.firePropertyChange("productList", oldProductList, new ArrayList<>(productList));
        } catch (StockException e) {
            DEBUG.error("ProductsListModel.loadProducts()\n%s", e.getMessage());
        }
    }

    /**
     * Get the list of all products.
     *
     * @return List of products
     */
    public List<Product> getProductList() {
        System.out.printf("%-10s %-25s %-10s %-10s%n", "ProductNo", "Description", "Price", "Quantity");
        System.out.println("----------------------------------------------------------");
        for (Product product : productList) {
            System.out.printf("%-10s %-25s $%-9.2f %-10d%n",
                    product.getProductNum(),
                    product.getDescription(),
                    product.getPrice(),
                    product.getQuantity());
        }
        return productList;
    }
    

    /**
     * Add a property change listener.
     *
     * @param listener PropertyChangeListener instance
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    /**
     * Remove a property change listener.
     *
     * @param listener PropertyChangeListener instance
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}