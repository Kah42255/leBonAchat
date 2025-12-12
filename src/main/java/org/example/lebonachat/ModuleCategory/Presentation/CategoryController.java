package org.example.lebonachat.ModuleCategory.Presentation;

import lombok.RequiredArgsConstructor;
import org.example.lebonachat.ModuleCategory.Metier.Category;
import org.example.lebonachat.ModuleCategory.Service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/category")
public class CategoryController {

    private final CategoryService service;

    @GetMapping("/list")
    public String list(Model model) {
        model.addAttribute("categories", service.getAll());
        return "category_list";
    }

    @GetMapping("/new")
    public String newCategory(Model model) {
        model.addAttribute("category", new Category());
        return "category_form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Category category) {
        service.save(category);
        return "redirect:/category/list";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/category/list";
    }
}
