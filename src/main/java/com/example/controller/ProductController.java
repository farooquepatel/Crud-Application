package com.example.controller;

import com.example.entity.Product;
import com.example.apiresponse.ApiResponse;
import com.example.entity.Category;
import com.example.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ProductController {

	@Autowired
	private ProductRepository productRepository;

	// Get All Products
	@GetMapping("/products")
	public ResponseEntity<ApiResponse<Map<String, Object>>> getAllProducts(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Page<Product> productsPage = productRepository.findAll(PageRequest.of(page, size));

		ApiResponse<Map<String, Object>> response;
		if (productsPage.hasContent()) {
			Map<String, Object> responseData = new HashMap<>();
			responseData.put("count", productsPage.getTotalElements());
			responseData.put("products", productsPage.getContent());

			response = new ApiResponse<>(200, "Products retrieved successfully", responseData);
			return ResponseEntity.ok(response);
		} else {
			response = new ApiResponse<>(204, "No products found", null);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
		}
	}

	// Get Product by ID
	@GetMapping("/products/{id}")
	public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable long id) {
	    Optional<Product> productOpt = productRepository.findById(id);

	    ApiResponse<Product> response;
	    if (productOpt.isPresent()) {
	        Product product = productOpt.get();
	       
	        product.getCategory().getId(); 
	        
	        response = new ApiResponse<>(200, "Product found", product);
	        return ResponseEntity.ok(response);
	    } else {
	        response = new ApiResponse<>(404, "Product with id " + id + " not found", null);
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	    }
	}


	// Add Product
	@PostMapping("/products")
	public ResponseEntity<ApiResponse<Void>> addProduct(@RequestBody Product product) {
		try {
			productRepository.save(product);
			ApiResponse<Void> response = new ApiResponse<>(201, "Product created successfully", null);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			ApiResponse<Void> response = new ApiResponse<>(400, "Error creating product", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	// Update Product
	@PutMapping("/products/{id}")
	public ResponseEntity<ApiResponse<Void>> updateProduct(@PathVariable long id, @RequestBody Product product) {
		Optional<Product> existingProductOpt = productRepository.findById(id);

		ApiResponse<Void> response;
		if (existingProductOpt.isPresent()) {
			Product existingProduct = existingProductOpt.get();
			existingProduct.setName(product.getName());
			existingProduct.setPrice(product.getPrice());

			// If the category is provided, update it
			if (product.getCategory() != null) {
				Optional<Category> categoryOpt = Optional.ofNullable(product.getCategory());
				if (categoryOpt.isPresent()) {
					existingProduct.setCategory(categoryOpt.get());
				} else {
					response = new ApiResponse<>(400, "Category not found", null);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
				}
			}

			productRepository.save(existingProduct);
			response = new ApiResponse<>(204, "Product updated successfully", null);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
		} else {
			response = new ApiResponse<>(404, "Product with id " + id + " not found", null);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

	// Delete Product
	@DeleteMapping("/products/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable long id) {
		Optional<Product> productOpt = productRepository.findById(id);

		ApiResponse<Void> response;
		if (productOpt.isPresent()) {
			productRepository.delete(productOpt.get());
			response = new ApiResponse<>(204, "Product with id " + id + " deleted", null);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
		} else {
			response = new ApiResponse<>(404, "Product with id " + id + " not found", null);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}
}
