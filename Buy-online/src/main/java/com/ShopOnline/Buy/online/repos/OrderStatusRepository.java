package com.ShopOnline.Buy.online.repos;

import com.ShopOnline.Buy.online.entities.order.OrderProduct;
import com.ShopOnline.Buy.online.entities.order.OrderStatus;
import com.ShopOnline.Buy.online.entities.product.ProductVariation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderStatusRepository extends CrudRepository<OrderStatus, Long> {

    List<OrderStatus> findByOrderProduct(OrderProduct orderProduct);
}
