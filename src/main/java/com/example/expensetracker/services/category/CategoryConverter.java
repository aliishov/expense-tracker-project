package com.example.expensetracker.services.category;

import com.example.expensetracker.dtos.categoyDto.CategoryRequestDto;
import com.example.expensetracker.dtos.categoyDto.CategoryResponseDto;
import com.example.expensetracker.models.transaction.Category;
import com.example.expensetracker.models.transaction.Type;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryConverter {
    public Category convertToDomainCategory(@Valid CategoryRequestDto categoryRequestDto) {
        return Category.builder()
                .name(categoryRequestDto.name())
                .type(Type.valueOf(categoryRequestDto.type()))
                .userId(categoryRequestDto.userId())
                .build();
    }

    public CategoryResponseDto convertToCategoryResponse(Category newCategory) {
        return new CategoryResponseDto(
                newCategory.getName(),
                newCategory.getType().name()
        );
    }
}
