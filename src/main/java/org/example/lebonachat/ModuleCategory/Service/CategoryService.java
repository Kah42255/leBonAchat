package org.example.lebonachat.ModuleCategory.Service;

import lombok.RequiredArgsConstructor;
import org.example.lebonachat.ModuleCategory.Metier.Category;
import org.example.lebonachat.ModuleCategory.Repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repo;

    public List<Category> getAll() {
        return repo.findAll();
    }

    public Category save(Category category) {
        return repo.save(category);
    }

    public Category getById(Long id) {
        return repo.findById(id).orElse(null);
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }
}
