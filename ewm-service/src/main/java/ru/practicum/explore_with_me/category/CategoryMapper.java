package ru.practicum.explore_with_me.category;

import org.springframework.stereotype.Component;
import ru.practicum.explore_with_me.category.dto.CategoryDto;
import ru.practicum.explore_with_me.category.dto.NewCategoryDto;

@Component
public class CategoryMapper {

    public static Category makeCategory(NewCategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName()).build();
    }

    public static CategoryDto makeCategoryDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName()).build();
    }
}
