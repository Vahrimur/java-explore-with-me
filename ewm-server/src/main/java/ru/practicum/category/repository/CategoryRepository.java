package ru.practicum.category.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.category.model.Category;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(value = "select * from categories order by id asc offset ?1 rows fetch next ?2 rows only",
            nativeQuery = true)
    List<Category> findAllByParams(int from, int size);
}
