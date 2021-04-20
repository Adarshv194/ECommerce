package com.ShopOnline.Buy.online.services;

import com.ShopOnline.Buy.online.entities.Customer;
import com.ShopOnline.Buy.online.entities.order.ConsolidatedOrder;
import com.ShopOnline.Buy.online.entities.order.OrderProduct;
import com.ShopOnline.Buy.online.entities.product.ProductReview;
import com.ShopOnline.Buy.online.exceptions.BadRequestException;
import com.ShopOnline.Buy.online.exceptions.ResourceNotFoundException;
import com.ShopOnline.Buy.online.models.ProductReviewAddModel;
import com.ShopOnline.Buy.online.repos.ConsolidatedOrderRepository;
import com.ShopOnline.Buy.online.repos.OrderProductRepository;
import com.ShopOnline.Buy.online.repos.ProductReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductReviewDaoService {

    @Autowired
    ProductReviewRepository productReviewRepository;
    @Autowired
    ConsolidatedOrderRepository consolidatedOrderRepository;
    @Autowired
    OrderProductRepository orderProductRepository;

    public String addProductReview(Customer customer, ProductReviewAddModel productReviewAddModel, Long orderProductId) {
        Optional<OrderProduct> orderProductOptional = orderProductRepository.findById(orderProductId);
        Boolean flag = false;

        if (orderProductOptional.isPresent()) {
            List<ConsolidatedOrder> consolidatedOrderList = consolidatedOrderRepository.findByCustomer(customer);

            if (consolidatedOrderList.size() == 0)
                throw new BadRequestException("There is no order placed by the customer " + customer.getFirstName() + " ");

            for (ConsolidatedOrder consolidatedOrder : consolidatedOrderList) {
                List<OrderProduct> orderProductList = orderProductRepository.findByConsolidatedOrderId(consolidatedOrder.getOrderId());

                if (orderProductList.size() != 0) {
                    for (OrderProduct orderProduct : orderProductList) {
                        if (orderProduct.getOrderProductId().equals(orderProductId)) {
                            flag = true;
                            break;
                        }
                    }
                }
            }

            if(flag == true) {
                OrderProduct orderProduct = orderProductOptional.get();

                ProductReview productReview = new ProductReview();
                productReview.setReview(productReviewAddModel.getReview());
                productReview.setRating(productReviewAddModel.getRating());
                productReview.setCustomer(customer);
                productReview.setProductVariation(orderProduct.getProductVariation());

                productReviewRepository.save(productReview);

                return "Product review has been added for the product " + orderProduct.getProductVariation().getVariantName() + " by the customer " + customer.getFirstName() + " for the order ID" + orderProductId + " ";
            }
            else {
                throw new BadRequestException("The order product ID " + orderProductId + " is not associated with the customer " + customer.getFirstName() + " ");
            }
        }
        else {
            throw new ResourceNotFoundException("Invalid order product ID" + orderProductId + ", No order product is found in the database");
        }
    }
}
