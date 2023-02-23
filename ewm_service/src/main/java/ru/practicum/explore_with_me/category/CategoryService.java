package ru.practicum.explore_with_me.category;

import ru.practicum.explore_with_me.category.dto.CategoryDto;
import ru.practicum.explore_with_me.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto saveNewCategory(NewCategoryDto categoryDto);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(long catId);

    CategoryDto updateCategory(CategoryDto categoryDto);

    void deleteCategory(long catId);
}
