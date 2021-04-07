package com.ShopOnline.Buy.online.repos;

import com.ShopOnline.Buy.online.entities.category.Category;
import com.ShopOnline.Buy.online.entities.category.CategoryMetaDataField;
import com.ShopOnline.Buy.online.entities.category.CategoryMetaDataFieldValues;
import com.ShopOnline.Buy.online.utils.CategoryMetaDataFieldValuesID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CategoryMetadataFieldValuesRepository extends CrudRepository<CategoryMetaDataFieldValues, CategoryMetaDataFieldValuesID> {

    Optional<CategoryMetaDataFieldValues> findByCategory(Category category);

    @Query(value = "select * from category_meta_data_field_values where category_category_id=:categoryId",nativeQuery = true)
    List<CategoryMetaDataFieldValues> findByCategoryId(@Param("categoryId") Long categoryId);

    Optional<CategoryMetaDataFieldValues> findByCategoryMetaDataField(CategoryMetaDataField categoryMetaDataField);

    @Query(value = "select * from category_meta_data_field_values where category_category_id=:category_id and category_meta_data_field_category_meta_data_field_id=:category_mdf_id",nativeQuery = true)
    Optional<CategoryMetaDataFieldValues> findByCategoryAndCategoryMetadataField(@Param("category_id") Long category_id, @Param("category_mdf_id") Long category_mdf_id);

    List<CategoryMetaDataFieldValues> findAll(Pageable pageable);
}
