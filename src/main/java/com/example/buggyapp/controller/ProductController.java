package com.example.buggyapp.controller;

import com.example.buggyapp.model.Product;
import com.example.buggyapp.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    // DEFECT: No pagination for potentially large datasets
    @GetMapping
    public List<Product> getAllProducts() {
        // DEFECT: Loading all products into memory
        return productService.getProducts(true);
    }

    // DEFECT: Using String parameter instead of boolean
    @GetMapping("/filter")
    public List<Product> getFilteredProducts(@RequestParam String includeOutOfStock) {
        // DEFECT: Unsafe string to boolean conversion
        boolean include = includeOutOfStock.equals("true");
        return productService.getProducts(include);
    }

    // DEFECT: Mass assignment vulnerability
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        // DEFECT: No input validation
        // DEFECT: Accepting all fields from request body
        return productService.createProduct(
                product.getName(),
                product.getPrice(),
                product.getQuantity(),
                product.getDescription(),
                product.getCategory()
        );
    }

    // DEFECT: Unsafe search - potential SQL injection
    @GetMapping("/search")
    public List<Product> searchProducts(@RequestParam String query) {
        // DEFECT: Passing unsanitized user input to service
        return productService.searchProductsUnsafe(query);
    }

    // DEFECT: No input validation on price update
    @PatchMapping("/{id}/price")
    public Product updatePrice(@PathVariable Long id, @RequestParam double newPrice) {
        // DEFECT: Allowing negative prices
        // DEFECT: No authorization check
        Product product = productService.updateProduct(
                id,
                null, // DEFECT: Passing null values
                newPrice,
                null,
                null,
                null
        );
        return product;
    }

    // DEFECT: Dangerous bulk operation without confirmation
    @PostMapping("/prices/increase")
    public void increaseAllPrices(@RequestParam double percentage) {
        // DEFECT: No validation on percentage value
        // DEFECT: No authorization or confirmation required
        productService.updateAllProductPrices(percentage);
    }

    // DEFECT: Missing error handling
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id) {
        // DEFECT: No check if product exists
        // DEFECT: No authorization
        // DEFECT: No response entity or status code
    }

    // DEFECT: Exposing internal implementation details
    @GetMapping("/stats")
    public String getStats() {
        // DEFECT: Potential division by zero
        double avgPrice = productService.calculateAveragePrice();

        // DEFECT: Exposing too much internal information
        return "Total products: " + productService.countProducts() +
               ", Average price: " + avgPrice +
               ", Database: jdbc:h2:mem:testdb";
    }

    // DEFECT: Inefficient endpoint
    @GetMapping("/category/{category}")
    public List<Product> getByCategory(@PathVariable String category) {
        // DEFECT: No URL encoding validation
        // DEFECT: Case-sensitive search without normalization
        return productService.getProductsByCategory(category);
    }

    // DEFECT: Magic numbers in code
    @GetMapping("/{id}/discount")
    public double getDiscountedPrice(@PathVariable Long id, @RequestParam String customerType) {
        Product product = productService.getProductsByCategory("").get(0); // DEFECT: Wrong implementation

        // DEFECT: Multiple magic numbers
        // DEFECT: String comparison without null check
        if (customerType.equals("VIP")) {
            return product.getPrice() * 0.8; // 20% discount
        } else if (customerType.equals("REGULAR")) {
            return product.getPrice() * 0.95; // 5% discount
        }

        return product.getPrice();
    }

    // DEFECT: Returning raw exception message
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        try {
            // DEFECT: This will throw if not found
            return productService.getProductsByCategory("").stream()
                    .filter(p -> p.getId().equals(id))
                    .findFirst()
                    .get(); // DEFECT: Using get() without isPresent() check
        } catch (Exception e) {
            // DEFECT: Catching generic exception
            // DEFECT: Returning null on error
            return null;
        }
    }

    // DEFECT: Duplicate code from createProduct
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        return productService.updateProduct(
                id,
                product.getName(),
                product.getPrice(),
                product.getQuantity(),
                product.getDescription(),
                product.getCategory()
        );
    }
}
