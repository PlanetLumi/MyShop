package testing;

import catalogue.Product;
import clients.accounts.ProductDB;
import middle.StockReadWriter;
import middle.StockException;

import javax.swing.*;
import java.sql.SQLException;
import java.util.List;

public class InMemoryStockReadWriter implements StockReadWriter {

    public void someMethod() throws StockException, SQLException {
    }

    @Override
    public boolean buyStock(String pNum, int amount) throws StockException {
        return false;
    }

    @Override
    public void addStock(String pNum, int amount) throws StockException {

    }

    @Override
    public void modifyStock(Product detail) throws StockException {

    }

    @Override
    public boolean exists(String pNum) throws StockException {
        return false;
    }

    @Override
    public Product getDetails(String pNum) throws StockException {
        return null;
    }

    @Override
    public ImageIcon getImage(String pNum) throws StockException {
        return null;
    }

    @Override
    public List<Object[]> findProduct(String pNum) throws StockException, SQLException {
        return List.of();
    }

    // Add any other methods required by the interface
}