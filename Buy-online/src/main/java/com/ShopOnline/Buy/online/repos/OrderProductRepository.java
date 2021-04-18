package com.ShopOnline.Buy.online.repos;

import com.ShopOnline.Buy.online.entities.order.OrderProduct;
import com.ShopOnline.Buy.online.entities.product.ProductVariation;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderProductRepository extends CrudRepository<OrderProduct, Long> {

    @Query(value = "select * from order_product where order_id=:orderId",nativeQuery = true)
    List<OrderProduct> findByConsolidatedOrderId(@Param("orderId") Long orderId);

    @Modifying
    @Query(value = "delete from order_product where order_id=:orderId",nativeQuery = true)
    void deleteAllProductOrder(@Param("orderId") Long orderId);

    List<OrderProduct> findByProductVariation(ProductVariation productVariation);

    List<OrderProduct> findAll();

}
