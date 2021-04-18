package com.ShopOnline.Buy.online.repos;

import com.ShopOnline.Buy.online.entities.Customer;
import com.ShopOnline.Buy.online.entities.order.ConsolidatedOrder;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsolidatedOrderRepository extends CrudRepository<ConsolidatedOrder, Long> {

    @Query(value = "select * from consolidated_order where order_id=:orderId and customer_user_id=:customerId",nativeQuery = true)
    List<Optional<ConsolidatedOrder>> findByOrderIdAndCustomerId(@Param("orderId") Long orderId, @Param("customerId") Long customerId);

    List<ConsolidatedOrder> findByCustomer(Customer customer);

    @Modifying
    @Query(value = "delete from consolidated_order where order_id=:orderId and customer_user_id=:customerId",nativeQuery = true)
    void deleteAllConsolidatedOrder(@Param("customerId") Long customerId,@Param("orderId") Long orderId);
}
