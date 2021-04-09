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

    @Query(value = "select * from category where parent_id is not NULL", nativeQuery = true)
    List<Category> findAllRootLevelCategory();

    @Query(value = "select * from category where parent_id is NULL", nativeQuery = true)
    List<Category> findRoot();

    @Query(value = "select * from category where parent_id is not NULL", nativeQuery = true)
    List<Category> findAllCategories();

    @Query(value = "select * from category where parent_id=:parentId", nativeQuery = true)
    List<Category> getAllSubCategoriesWithId(@Param("parentId") Long parentId);

    @Query(value = "select name from category where parent_id=:categoryId", nativeQuery = true)
    List<String> checkForCategoryName(@Param("categoryId") Long categoryId);

    @Query(value = "select category_id from category where parent_id=:categoryId", nativeQuery = true)
    List<Long> findAllChildCategoriesId(@Param("categoryId") Long categoryId);

    @Query(value = "select * from category where category_id=:categoryId", nativeQuery = true)
    List<Category> findAllParentCategoryWithSubCategoryId(@Param("categoryId") Long categoryId);
}
