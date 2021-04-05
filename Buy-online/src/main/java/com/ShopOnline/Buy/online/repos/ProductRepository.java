package com.ShopOnline.Buy.online.repos;

import com.ShopOnline.Buy.online.entities.product.Product;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends CrudRepository<Product, Long> {

    @Query(value = "select product_name from product where product_name=:productName and brand=:brand and category_id=:category_id and seller_user_id=:seller_id",nativeQuery = true)
    String checkForUniqueness(@Param("productName") String productName, @Param("brand") String brand, @Param("category_id") Long category_id, @Param("seller_id") Long seller_id);
}
