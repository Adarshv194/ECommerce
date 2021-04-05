package com.ShopOnline.Buy.online.repos;

import com.ShopOnline.Buy.online.entities.category.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    Optional<Category> findByName(String name);

    List<Category> findAll(Pageable pageable);

    @Query(value = "select * from category where parent_id is not NULL",nativeQuery = true)
    List<Category> findAllRootLevelCategory();

    @Query(value = "select * from category where parent_id=:parent",nativeQuery = true)
    List<Category> getAllSubCategoriesWithId(@Param("parent") Long parent);
}
