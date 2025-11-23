package com.example.buggyapp.service;

import com.example.buggyapp.model.Product;
import com.example.buggyapp.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    // DEFECT: Missing @Autowired or proper dependency injection documentation
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // DEFECT: SQL Injection vulnerability - building query with string concatenation
    public List<Product> searchProductsUnsafe(String searchTerm) {
        try {
            // DEFECT: Hardcoded database credentials
            Connection conn = DriverManager.getConnection(
                    "jdbc:h2:mem:testdb", "admin", "admin123");

            Statement stmt = conn.createStatement();

            // DEFECT: SQL Injection - concatenating user input directly
            String query = "SELECT * FROM products WHERE name LIKE '%" + searchTerm + "%'";

            ResultSet rs = stmt.executeQuery(query);

            // DEFECT: Not closing resources (connection, statement, resultset)
            return productRepository.findAll();

        } catch (Exception e) {
            // DEFECT: Catching generic Exception and swallowing it
            e.printStackTrace();
            return null;
        }
    }

    // DEFECT: Method doing multiple unrelated things
    public Product createProduct(String name, double price, Integer quantity, String description, String category) {
        // DEFECT: No input validation
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setQuantity(quantity);
        product.setDescription(description);
        product.setCategory(category);

        // DEFECT: System.out.println in production code
        System.out.println("Creating product: " + name);

        // DEFECT: Saving and immediately fetching all products (inefficient)
        Product saved = productRepository.save(product);
        productRepository.findAll();

        return saved;
    }

    // DEFECT: Code duplication with createProduct
    public Product updateProduct(Long id, String name, double price, Integer quantity, String description, String category) {
        Product product = productRepository.findById(id).orElse(null);

        if (product != null) {
            product.setName(name);
            product.setPrice(price);
            product.setQuantity(quantity);
            product.setDescription(description);
            product.setCategory(category);

            // DEFECT: Duplicate System.out.println
            System.out.println("Updating product: " + name);

            return productRepository.save(product);
        }

        // DEFECT: Returning null instead of throwing exception or using Optional
        return null;
    }

    // DEFECT: N+1 query problem potential
    public void updateAllProductPrices(double percentage) {
        List<Product> products = productRepository.findAll();

        for (Product product : products) {
            // DEFECT: Calling save in a loop (N+1 problem)
            product.setPrice(product.getPrice() * percentage);
            productRepository.save(product);
        }
    }

    // DEFECT: Method with boolean parameter (code smell)
    public List<Product> getProducts(boolean includeOutOfStock) {
        List<Product> allProducts = productRepository.findAll();

        if (includeOutOfStock) {
            return allProducts;
        } else {
            // DEFECT: Inefficient filtering in application layer instead of database
            return allProducts.stream()
                    .filter(p -> p.getQuantity() > 0)
                    .toList();
        }
    }

    // DEFECT: Potential division by zero
    public double calculateAveragePrice() {
        List<Product> products = productRepository.findAll();
        double total = 0;

        for (Product product : products) {
            total += product.getPrice();
        }

        // DEFECT: No check if products list is empty (division by zero)
        return total / products.size();
    }

    // DEFECT: Inefficient implementation - loading all products into memory
    public long countProducts() {
        // DEFECT: Using getAllProductsForCount which loads all products
        return productRepository.getAllProductsForCount().size();
    }

    // DEFECT: Mutable return type
    public List<Product> getProductsByCategory(String category) {
        // DEFECT: Returning mutable list directly
        return productRepository.findByCategory(category);
    }

    // DEFECT: Using nested ternary operators (poor readability)
    public String getProductStatus(Product product) {
        return product.getQuantity() > 100 ? "In Stock" :
                product.getQuantity() > 10 ? "Low Stock" :
                product.getQuantity() > 0 ? "Very Low Stock" : "Out of Stock";
    }

    // DEFECT: Empty method body
    public void notifyLowStock(Product product) {
        // TODO: Implement notification logic
        // DEFECT: Empty method body in production code
    }

    // DEFECT: Method complexity too high
    public boolean validateProduct(Product product) {
        if (product == null) {
            return false;
        }
        if (product.getName() == null) {
            return false;
        }
        if (product.getName().isEmpty()) {
            return false;
        }
        if (product.getPrice() < 0) {
            return false;
        }
        if (product.getQuantity() == null) {
            return false;
        }
        if (product.getQuantity() < 0) {
            return false;
        }
        if (product.getCategory() == null) {
            return false;
        }
        if (product.getCategory().isEmpty()) {
            return false;
        }
        return true;
    }
}
