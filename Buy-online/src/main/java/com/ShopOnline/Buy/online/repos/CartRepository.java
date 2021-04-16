package com.ShopOnline.Buy.online.repos;

import com.ShopOnline.Buy.online.entities.Customer;
import com.ShopOnline.Buy.online.entities.order.Cart;
import com.ShopOnline.Buy.online.entities.product.ProductVariation;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends CrudRepository<Cart, Long> {

    @Query(value = "select * from cart where customer_id=:customerId and product_variation_id=:productVariationId", nativeQuery = true)
    List<Optional<Cart>> findByCustomerAndProductVariationId(@Param("customerId") Long customerId, @Param("productVariationId") Long productVariationId);

    @Query(value = "select product_variation_id from cart where customer_id=:customerId",nativeQuery = true)
    List<Long> findProductVariationId(@Param("customerId") Long customerId);

    Optional<Cart> findByProductVariation(ProductVariation productVariation);

    List<Cart> findByCustomer(Customer customer);
}
