package com.example.expensetracker.services.category;

import com.example.expensetracker.dtos.categoyDto.CategoryRequestDto;
import com.example.expensetracker.dtos.categoyDto.CategoryResponseDto;
import com.example.expensetracker.models.transaction.Category;
import com.example.expensetracker.models.enums.Type;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryConverter {
    public Category convertToDomainCategory(@Valid CategoryRequestDto categoryRequestDto, UUID userId) {
        return Category.builder()
                .name(categoryRequestDto.name())
                .type(Type.valueOf(categoryRequestDto.type()))
                .userId(userId)
                .build();
    }

    public CategoryResponseDto convertToCategoryResponse(Category newCategory) {
        return new CategoryResponseDto(
                newCategory.getName(),
                newCategory.getType().name()
        );
    }
}
