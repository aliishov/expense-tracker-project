package com.example.expensetracker.services.category;

import com.example.expensetracker.dtos.categoyDto.CategoryRequestDto;
import com.example.expensetracker.dtos.categoyDto.CategoryResponseDto;
import com.example.expensetracker.dtos.categoyDto.CategoryUpdateDto;
import com.example.expensetracker.models.transaction.Category;
import com.example.expensetracker.models.transaction.Type;
import com.example.expensetracker.repositories.CategoryRepository;
import com.example.expensetracker.utils.SecurityUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryConverter categoryConverter;

    private final static Marker MY_LOG_MARKER = MarkerFactory.getMarker("MY_LOGGER");
    private final static Logger LOGGER = LoggerFactory.getLogger("MY_LOGGER");

    public ResponseEntity<CategoryResponseDto> create(@Valid CategoryRequestDto categoryRequestDto, UUID userId) {
        LOGGER.info(MY_LOG_MARKER, "Creating new category with name: {}", categoryRequestDto.name());

        Category newCategory = categoryConverter.convertToDomainCategory(categoryRequestDto, userId);
        categoryRepository.save(newCategory);

        CategoryResponseDto categoryResponseDto = categoryConverter.convertToCategoryResponse(newCategory);

        LOGGER.info(MY_LOG_MARKER, "Category with name: {} created successfully", categoryResponseDto.name());
        return ResponseEntity.ok(categoryResponseDto);
    }

    public ResponseEntity<CategoryResponseDto> update(String categoryName,
                                                      @Valid CategoryUpdateDto categoryUpdateDto,
                                                      UUID userId) {
        LOGGER.info(MY_LOG_MARKER, "Updating category with Name: {}", categoryName);

        Category category = getCategoryByName(categoryName);

        SecurityUtil.checkCategoryOwnership(category, userId);

        category.setName((categoryUpdateDto.name() != null) ? categoryUpdateDto.name() : category.getName());
        category.setType((categoryUpdateDto.type() != null) ? Type.valueOf(categoryUpdateDto.type()) : category.getType());

        CategoryResponseDto categoryResponseDto = categoryConverter.convertToCategoryResponse(category);

        LOGGER.info(MY_LOG_MARKER, "Category with Name: {} updated successfully", categoryName);
        return ResponseEntity.ok(categoryResponseDto);
    }

    public ResponseEntity<Void> delete(String categoryName, UUID userId) {
        LOGGER.info(MY_LOG_MARKER, "Deleting category with Name: {}", categoryName);

        Category category = getCategoryByName(categoryName);

        SecurityUtil.checkCategoryOwnership(category, userId);

        categoryRepository.delete(category);

        LOGGER.info(MY_LOG_MARKER, "Category with Name: {} deleted successfully", categoryName);
        return ResponseEntity.noContent().build();
    }

    private Category getCategoryByName(String categoryName) {
        return categoryRepository.findByName(categoryName)
                .orElseThrow(() -> {
                    LOGGER.info(MY_LOG_MARKER, "Category with Name: {} not found", categoryName);
                    return new EntityNotFoundException("Category not found");
                });
    }
}
