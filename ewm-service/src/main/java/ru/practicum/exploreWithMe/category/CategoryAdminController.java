package ru.practicum.exploreWithMe.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.category.dto.CategoryDto;
import ru.practicum.exploreWithMe.category.dto.NewCategoryDto;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class CategoryAdminController {

    private final CategoryService service;

    @PostMapping
    public CategoryDto saveNewCategory(@RequestBody @Validated NewCategoryDto categoryDto) {
        return service.saveNewCategory(categoryDto);
    }

    @PatchMapping
    public CategoryDto updateCategory(@RequestBody @Validated CategoryDto categoryDto) {
        return service.updateCategory(categoryDto);
    }

    @DeleteMapping("{catId}")
    public void deleteCategory(@PathVariable int catId) {
        service.deleteCategory(catId);
    }
}
