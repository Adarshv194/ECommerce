package com.ShopOnline.Buy.online.repos;

import com.ShopOnline.Buy.online.entities.product.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {

    @Query(value = "select product_name from product where product_name=:productName and brand=:brand and category_id=:category_id and seller_user_id=:seller_id",nativeQuery = true)
    String checkForUniqueness(@Param("productName") String productName, @Param("brand") String brand, @Param("category_id") Long category_id, @Param("seller_id") Long seller_id);

    @Query(value = "select * from product where seller_user_id=:sellerId and is_deleted=false and is_active=true",nativeQuery = true)
    List<Product> findSellerAssociatedProducts(@Param("sellerId") Long sellerId);

    @Query(value = "select * from product where category_id=:categoryId and is_deleted=false and is_active=true", nativeQuery = true)
    List<Product> findProductByCategory(@Param("categoryId") Long categoryId);

    @Query(value = "select * from product where is_deleted=false and is_active=true", nativeQuery = true)
    List<Product> findAllProducts();
}
