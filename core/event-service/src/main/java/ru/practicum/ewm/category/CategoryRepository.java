package ru.practicum.ewm.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface CategoryRepository extends JpaRepository<Category, Long> {

    @Modifying
    @Query("delete from Category c where c.id = :id")
    int delete(@Param("id") long id);
}
