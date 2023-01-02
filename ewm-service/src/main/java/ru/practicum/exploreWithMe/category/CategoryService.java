package ru.practicum.exploreWithMe.category;

import ru.practicum.exploreWithMe.category.dto.CategoryDto;
import ru.practicum.exploreWithMe.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto saveNewCategory(NewCategoryDto categoryDto);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategoryById(long catId);

    CategoryDto updateCategory(CategoryDto categoryDto);

    void deleteCategory(long catId);
}
