package com.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.apiresponse.ApiResponse;
import com.example.entity.Category;
import com.example.repository.CategoryRepository;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class CategoryController {

	@Autowired
	private CategoryRepository categoryRepository;

	// Get All Categories
	@GetMapping("/categories")
	public ResponseEntity<ApiResponse<Map<String, Object>>> getAllCategory(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {

		Page<Category> categoriesPage = categoryRepository.findAll(PageRequest.of(page, size));

		ApiResponse<Map<String, Object>> response;
		if (categoriesPage.hasContent()) {
			Map<String, Object> responseData = new HashMap<>();
			responseData.put("count", categoriesPage.getTotalElements());
			responseData.put("category", categoriesPage.getContent());

			response = new ApiResponse<>(200, "Categories retrieved successfully", responseData);
			return ResponseEntity.ok(response);
		} else {
			response = new ApiResponse<>(204, "No categories found", null);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
		}
	}

	// Get Category by ID
	@GetMapping("/categories/{id}")
	public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable long id) {
		Optional<Category> categoryOpt = categoryRepository.findById(id);

		ApiResponse<Category> response;
		if (categoryOpt.isPresent()) {
			response = new ApiResponse<>(200, "Category found", categoryOpt.get());
			return ResponseEntity.ok(response);
		} else {
			response = new ApiResponse<>(404, "Category with id " + id + " not found", null);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

	// Add Category
	@PostMapping("/categories")
	public ResponseEntity<ApiResponse<Void>> addCategory(@RequestBody Category category) {
		try {
			categoryRepository.save(category);
			ApiResponse<Void> response = new ApiResponse<>(201, "Category created successfully", null);
			return ResponseEntity.status(HttpStatus.CREATED).body(response);
		} catch (Exception e) {
			ApiResponse<Void> response = new ApiResponse<>(400, "Error creating category", null);
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}

	// Update Category
	@PutMapping("/categories/{id}")
	public ResponseEntity<ApiResponse<Void>> updateCategory(@PathVariable long id, @RequestBody Category category) {
		Optional<Category> existingCategoryOpt = categoryRepository.findById(id);

		ApiResponse<Void> response;
		if (existingCategoryOpt.isPresent()) {
			Category existingCategory = existingCategoryOpt.get();
			existingCategory.setName(category.getName());
			categoryRepository.save(existingCategory);
			response = new ApiResponse<>(204, "Category updated successfully", null);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
		} else {
			response = new ApiResponse<>(404, "Category with id " + id + " not found", null);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}

	// Delete Category
	@DeleteMapping("/categories/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable long id) {
		Optional<Category> categoryOpt = categoryRepository.findById(id);

		ApiResponse<Void> response;
		if (categoryOpt.isPresent()) {
			categoryRepository.delete(categoryOpt.get());
			response = new ApiResponse<>(204, "Category with id " + id + " deleted", null);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(response);
		} else {
			response = new ApiResponse<>(404, "Category with id " + id + " not found", null);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}
	}
}
