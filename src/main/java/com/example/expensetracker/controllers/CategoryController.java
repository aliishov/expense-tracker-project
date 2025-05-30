package com.example.expensetracker.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions/categories")
@RequiredArgsConstructor
@Validated
@Tag(name = "Categories Controller",
        description = "Manage Categories in Project")
public class CategoryController {
}
