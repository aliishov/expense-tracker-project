package com.example.expensetracker.controllers;

import com.example.expensetracker.dtos.categoyDto.CategoryRequestDto;
import com.example.expensetracker.dtos.categoyDto.CategoryResponseDto;
import com.example.expensetracker.dtos.categoyDto.CategoryUpdateDto;
import com.example.expensetracker.services.category.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions/categories")
@RequiredArgsConstructor
@Validated
@Tag(name = "Categories Controller",
        description = "Manage Categories in Project")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponseDto> create(@Valid @RequestBody CategoryRequestDto categoryRequestDto) {
        return categoryService.create(categoryRequestDto);
    }

    @PutMapping("/{categoryName}")
    public ResponseEntity<CategoryResponseDto> update(@PathVariable String categoryName,
                                                      @RequestBody @Valid CategoryUpdateDto categoryUpdateDto) {
        return categoryService.update(categoryName, categoryUpdateDto);
    }

    @DeleteMapping("/{categoryName}")
    public ResponseEntity<Void> delete(@PathVariable String categoryName) {
        return categoryService.delete(categoryName);
    }
}
