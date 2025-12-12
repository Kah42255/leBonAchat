package org.example.lebonachat.ModuleCategory.Repository;


import org.example.lebonachat.ModuleCategory.Metier.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
