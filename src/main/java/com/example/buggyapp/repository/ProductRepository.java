package com.example.buggyapp.repository;

import com.example.buggyapp.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(String category);

    // DEFECT: Native query without proper parameterization
    @Query(value = "SELECT * FROM products WHERE name = ?1 OR category = ?1", nativeQuery = true)
    List<Product> searchProducts(String searchTerm);

    // DEFECT: Inefficient query - fetching all records when only count is needed
    @Query("SELECT p FROM Product p")
    List<Product> getAllProductsForCount();
}
