package ru.practicum.explore_with_me.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.explore_with_me.category.dto.CategoryDto;
import ru.practicum.explore_with_me.category.dto.NewCategoryDto;
import ru.practicum.explore_with_me.exception.ErrorStatus;
import ru.practicum.explore_with_me.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository repository;

    @Override
    public CategoryDto saveNewCategory(NewCategoryDto categoryDto) {
        return CategoryMapper.makeCategoryDto(repository.save(CategoryMapper.makeCategory(categoryDto)));
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        return repository.findAll(PageRequest.of(from / size, size))
                .stream()
                .map(CategoryMapper::makeCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Optional<Category> categoryOptional = repository.findById(catId);
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            return CategoryMapper.makeCategoryDto(category);
        } else {
            throw new NotFoundException(null, ErrorStatus.NOT_FOUND, "The required object was not found.",
                    String.format("Event with id=%s was not found.", catId),
                    LocalDateTime.now());
        }
    }

    @Override
    public CategoryDto updateCategory(CategoryDto categoryDto) {
        Optional<Category> categoryOptional = repository.findById(categoryDto.getId());
        if (categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            category.setName(categoryDto.getName());
            return CategoryMapper.makeCategoryDto(repository.save(category));
        } else {
            throw new NotFoundException(null, ErrorStatus.NOT_FOUND, "The required object was not found.",
                    String.format("Event with id=%s was not found.", categoryDto.getId()),
                    LocalDateTime.now());
        }
    }

    @Override
    public void deleteCategory(Long catId) {
        repository.deleteById(catId);
    }
}
