package com.ShopOnline.Buy.online.repos;

import com.ShopOnline.Buy.online.entities.category.CategoryMetaDataField;
import com.ShopOnline.Buy.online.entities.product.ProductVariation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface ProductVariationRepository extends CrudRepository<ProductVariation, Long> {

    @Query(value = "select category_meta_data_field_category_meta_data_field_id from category_meta_data_field_values where category_category_id=:categoryId",nativeQuery = true)
    List<Long> checkDbMetadataLengthField(@Param("categoryId") Long categoryId);

    @Query(value = "select product_attributes from product_variation where product_id=:productId",nativeQuery = true)
    List<String> findAllProductVariationAttributes(@Param("productId") Long productId);

    @Query(value = "select * from product_variation where product_id=:productId and variant_name=:productVariationName")
    List<ProductVariation> checkForProductvariationWithNameAndProductId(@Param("productVariationName") String productVariationName, @Param("productId") Long productId);
}
