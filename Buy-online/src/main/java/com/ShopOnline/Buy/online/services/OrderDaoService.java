package com.ShopOnline.Buy.online.services;

import com.ShopOnline.Buy.online.entities.Address;
import com.ShopOnline.Buy.online.entities.Customer;
import com.ShopOnline.Buy.online.entities.Seller;
import com.ShopOnline.Buy.online.entities.order.*;
import com.ShopOnline.Buy.online.entities.product.Product;
import com.ShopOnline.Buy.online.entities.product.ProductVariation;
import com.ShopOnline.Buy.online.exceptions.BadRequestException;
import com.ShopOnline.Buy.online.exceptions.ResourceNotFoundException;
import com.ShopOnline.Buy.online.models.AdminViewOrderModel;
import com.ShopOnline.Buy.online.models.OrderViewModel;
import com.ShopOnline.Buy.online.models.SellerViewOrderModel;
import com.ShopOnline.Buy.online.repos.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
public class OrderDaoService {

    @Autowired
    CartRepository cartRepository;
    @Autowired
    ProductVariationRepository productVariationRepository;
    @Autowired
    AddressRepository addressRepository;
    @Autowired
    ConsolidatedOrderRepository consolidatedOrderRepository;
    @Autowired
    OrderProductRepository orderProductRepository;
    @Autowired
    OrderStatusRepository orderStatusRepository;
    @Autowired
    ProductRepository productRepository;

    @Transactional
    public String orderAllProductsFromCart(Customer customer, Long addressId) {
        List<Cart> cartList = cartRepository.findByCustomer(customer);
        if (cartList.size() == 0)
            return customer.getFirstName() + " your cart is empty, To place an order please add the product variation in your cart or order directly";

        List<ProductVariation> productVariationList = new ArrayList<>();
        Integer totalProductsOrdered = 0;
        Integer amountPaid = 0;

        for (Cart cart : cartList) {
            ProductVariation productVariation = productVariationRepository.findById(cart.getProductVariation().getProductVariationId()).get();
            if (!productVariation.getActive() || productVariation.getDeleted() || productVariation.getProduct().getDeleted())
                throw new BadRequestException("Can not place the order as the " + productVariation.getVariantName() + " is not active or in deleted state by the seller");

            totalProductsOrdered = totalProductsOrdered + 1;
            Integer totalPaid = productVariation.getPrice() * cart.getQuantity();
            amountPaid += totalPaid;
            productVariationList.add(productVariation);
        }

        ConsolidatedOrder consolidatedOrder = new ConsolidatedOrder();
        consolidatedOrder.setCustomer(customer);
        consolidatedOrder.setAmountPaid((amountPaid.doubleValue()));
        consolidatedOrder.setTotalProductsOrdered(totalProductsOrdered);
        consolidatedOrder.setPaymentMethod("Cash on delivery");

        Optional<Address> addressOptional = addressRepository.findById(addressId);
        if (!addressOptional.isPresent()) {
            throw new BadRequestException("Invalid address id, No address found with the address id " + addressId + " ");
        } else {
            Address address = addressOptional.get();

            if (!address.getUser().getUserId().equals(customer.getUserId())) {
                throw new BadRequestException("The address provided with the address ID " + addressId + ", does not belongs to customer " + customer.getFirstName() + " ");
            }

            consolidatedOrder.setCustomerAddressCountry(address.getCountry());
            consolidatedOrder.setCustomerAddressState(address.getState());
            consolidatedOrder.setCustomerAddressCity(address.getCity());
            consolidatedOrder.setCustomerAddressLine(address.getAddressLine());
            consolidatedOrder.setCustomerAddressZipCode(address.getZipCode());
            consolidatedOrder.setCustomerAddressLabel(address.getLabel());
            consolidatedOrder.setSpecialInformation("Handle with care");
            consolidatedOrder.setDateCreated(new Date());

            consolidatedOrderRepository.save(consolidatedOrder);

            List<OrderProduct> orderProductList = new ArrayList<>();
            List<OrderStatus> orderStatusList = new ArrayList<>();

            for (Cart cart : cartList) {
                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setConsolidatedOrder(consolidatedOrder);
                orderProduct.setProductVariation(cart.getProductVariation());

                if (cart.getQuantity() > cart.getProductVariation().getQuantityAvailable())
                    throw new BadRequestException("Can not place the order for " + cart.getProductVariation().getVariantName() + " as the seller does not have " + cart.getQuantity() + " quantity in stock, You can order " + cart.getProductVariation().getQuantityAvailable() + " number of products for the same");

                orderProduct.setQuantity(cart.getQuantity().doubleValue());
                Integer totalAmount = cart.getProductVariation().getPrice() * cart.getQuantity();
                orderProduct.setPrice(totalAmount.doubleValue());

                orderProductList.add(orderProduct);

                OrderStatus orderStatus = new OrderStatus();
                orderStatus.setOrderProduct(orderProduct);
                orderStatus.setFromStatus(FromStatus.ORDER_PLACED);
                orderStatus.setTransitionNotesComments("from " + orderStatus.getFromStatus() + " to " + " not confirmed by the seller");

                orderStatusList.add(orderStatus);
            }

            cartRepository.deleteAllProducts(customer.getUserId());

            orderProductRepository.saveAll(orderProductList);

            orderStatusRepository.saveAll(orderStatusList);

            return "Your order has been placed with order ID " + consolidatedOrder.getOrderId() + ", The consolidated bill as well as an individual bill for the product has been created";
        }
    }

    @Transactional
    public String orderPartialProducts(Set<Long> productVariationIdSet, Customer customer, Long addressId) {
       if(productVariationIdSet.size() == 0)
           throw new BadRequestException("Invalid data format no variation is added, Please provide the variation to place an order");

       List<Cart> cartList = new ArrayList<>();
       Integer totalProductOrdered = 0;
       Integer totalAmountPaid = 0;

       for(Long id : productVariationIdSet) {
           Optional<ProductVariation> productVariationOptional = productVariationRepository.findById(id);
           if(productVariationOptional.isPresent()) {
               ProductVariation productVariation = productVariationOptional.get();

               List<Optional<Cart>> optionalCarList = cartRepository.findByCustomerAndProductVariationId(customer.getUserId(), id);

               if(optionalCarList.size() == 0)
                   throw new BadRequestException("No product variation is found in the cart for the customer " + customer.getFirstName() + " with the product variation id " + id + " ");

               if(optionalCarList.size() == 2)
                   throw new BadRequestException("Invalid data format is saved in the cart for the customer " + customer.getFirstName() + " with the product variation id ");

               Cart cart = optionalCarList.get(0).get();
               cartList.add(cart);
               totalProductOrdered = totalProductOrdered + 1;
               Integer totalPaidForProductVariation = productVariation.getPrice() * cart.getQuantity();
               totalAmountPaid = totalAmountPaid + totalPaidForProductVariation;
           }
           else {
               throw new ResourceNotFoundException("Invalid product variation ID " + id + ", No product variation is found in the database");
           }
       }

        Optional<Address> addressOptional = addressRepository.findById(addressId);
       if(!addressOptional.isPresent())
           throw new ResourceNotFoundException("Invalid address ID " + addressId + ", No address found in the database");

        Address address = addressOptional.get();

        if(!address.getUser().getUserId().equals(customer.getUserId()))
            throw new BadRequestException("The customer " + customer.getFirstName() + " is not associated with the address id " + addressId + " ");

        ConsolidatedOrder consolidatedOrder = new ConsolidatedOrder();
       consolidatedOrder.setCustomer(customer);
       consolidatedOrder.setTotalProductsOrdered(totalProductOrdered);
       consolidatedOrder.setAmountPaid(totalAmountPaid.doubleValue());
       consolidatedOrder.setDateCreated(new Date());
       consolidatedOrder.setSpecialInformation("Handle with care");
       consolidatedOrder.setPaymentMethod("Cash on delivery");
       consolidatedOrder.setCustomerAddressCountry(address.getCountry());
       consolidatedOrder.setCustomerAddressState(address.getState());
       consolidatedOrder.setCustomerAddressCity(address.getCity());
       consolidatedOrder.setCustomerAddressLine(address.getAddressLine());
       consolidatedOrder.setCustomerAddressZipCode(address.getZipCode());
       consolidatedOrder.setCustomerAddressLabel(address.getLabel());

       consolidatedOrderRepository.save(consolidatedOrder);

       List<OrderProduct> orderProductList = new ArrayList<>();
        List<OrderStatus> orderStatusList = new ArrayList<>();

       for(Cart cart : cartList) {
           OrderProduct orderProduct = new OrderProduct();
           orderProduct.setConsolidatedOrder(consolidatedOrder);
           orderProduct.setProductVariation(cart.getProductVariation());

           if(cart.getQuantity() > cart.getProductVariation().getQuantityAvailable())
               throw new BadRequestException("Can not place the order for " + cart.getProductVariation().getVariantName() + " as the seller does not have " + cart.getQuantity() + " quantity in stock, You can order " + cart.getProductVariation().getQuantityAvailable() + " number of products for the same");

           orderProduct.setQuantity(cart.getQuantity().doubleValue());

           Integer totalPaidForProductVariation = cart.getProductVariation().getPrice() * cart.getQuantity();
           orderProduct.setPrice(totalPaidForProductVariation.doubleValue());

           orderProductList.add(orderProduct);

           OrderStatus orderStatus = new OrderStatus();
           orderStatus.setOrderProduct(orderProduct);
           orderStatus.setFromStatus(FromStatus.ORDER_PLACED);
           orderStatus.setTransitionNotesComments("from " + orderStatus.getFromStatus() + " to " + " not confirmed by the seller");

           orderStatusList.add(orderStatus);

           cartRepository.deleteAllProductsByCustomerIdAndCartId(customer.getUserId(),cart.getCartId());
       }

       orderProductRepository.saveAll(orderProductList);

       orderStatusRepository.saveAll(orderStatusList);

        return "Your order has been placed with order ID " + consolidatedOrder.getOrderId() + ", The consolidated bill as well as an individual bill for the product has been created";
    }

    @Transactional
    public String orderProductDirectly(Customer customer, Long productVariationId, Integer quantity, Long addressId) {
        Optional<ProductVariation> productVariationOptional = productVariationRepository.findById(productVariationId);
        if (productVariationOptional.isPresent()) {
            ProductVariation productVariation = productVariationOptional.get();

            if (!productVariation.getActive() || productVariation.getDeleted() || productVariation.getProduct().getDeleted())
                throw new BadRequestException("Can not place the order as the " + productVariation.getVariantName() + " is not active or in deleted state by the seller");

            Optional<Address> addressOptional = addressRepository.findById(addressId);
            if (!addressOptional.isPresent()) {
                throw new BadRequestException("Invalid address id, No address found with the address id " + addressId + " ");
            } else {
                Address address = addressOptional.get();

                if (!address.getUser().getUserId().equals(customer.getUserId())) {
                    throw new BadRequestException("The address provided with the address ID " + addressId + ", does not belongs to customer " + customer.getFirstName() + " ");
                }

                ConsolidatedOrder consolidatedOrder = new ConsolidatedOrder();
                Integer amountPaid = productVariation.getPrice() * quantity;

                consolidatedOrder.setCustomer(customer);
                consolidatedOrder.setPaymentMethod("Cash on delivery");
                consolidatedOrder.setTotalProductsOrdered(1);
                consolidatedOrder.setAmountPaid(amountPaid.doubleValue());
                consolidatedOrder.setCustomerAddressCountry(address.getCountry());
                consolidatedOrder.setCustomerAddressState(address.getState());
                consolidatedOrder.setCustomerAddressCity(address.getCity());
                consolidatedOrder.setCustomerAddressLine(address.getAddressLine());
                consolidatedOrder.setCustomerAddressZipCode(address.getZipCode());
                consolidatedOrder.setCustomerAddressLabel(address.getLabel());
                consolidatedOrder.setSpecialInformation("Handle with care");
                consolidatedOrder.setDateCreated(new Date());

                consolidatedOrderRepository.save(consolidatedOrder);

                OrderProduct orderProduct = new OrderProduct();
                orderProduct.setConsolidatedOrder(consolidatedOrder);
                orderProduct.setProductVariation(productVariation);
                orderProduct.setPrice(amountPaid.doubleValue());

                if (quantity > productVariation.getQuantityAvailable())
                    throw new BadRequestException("Can not place the order for " + productVariation.getVariantName() + " as the seller does not have " + quantity + " quantity in stock, You can order " + productVariation.getQuantityAvailable() + " number of products for the same");

                orderProduct.setQuantity(quantity.doubleValue());

                orderProductRepository.save(orderProduct);

                OrderStatus orderStatus = new OrderStatus();
                orderStatus.setOrderProduct(orderProduct);
                orderStatus.setFromStatus(FromStatus.ORDER_PLACED);
                orderStatus.setTransitionNotesComments("from " + orderStatus.getFromStatus() + " to " + " not confirmed by the seller");

                orderStatusRepository.save(orderStatus);

                return "Your order has been placed with order ID " + consolidatedOrder.getOrderId() + ", The consolidated bill as well as an individual bill for the product has been created";
            }
        } else {
            throw new ResourceNotFoundException("Invalid product variation ID, no product variation is found with the id " + productVariationId + " ");
        }
    }

    @Transactional
    public String orderCancel(Customer customer, Long orderProductId) {
        Optional<OrderProduct> orderProductOptional = orderProductRepository.findById(orderProductId);
        Boolean flag = false;
        
        if(orderProductOptional.isPresent()) {
            OrderProduct orderProduct = orderProductOptional.get();

            List<ConsolidatedOrder> consolidatedOrderList = consolidatedOrderRepository.findByCustomer(customer);
            
            if(consolidatedOrderList.size() == 0) 
                throw new BadRequestException("No order is placed by the customer" + customer.getFirstName());
            
            for(ConsolidatedOrder consolidatedOrder : consolidatedOrderList) {
                List<OrderProduct> orderProductList = orderProductRepository.findByConsolidatedOrderId(consolidatedOrder.getOrderId());
                
                for(OrderProduct orderProductFetch : orderProductList) {
                    if(orderProductFetch.getOrderProductId().equals(orderProduct.getOrderProductId())) {
                        flag = true;
                        break;
                    }
                }
            }
            
            if(flag == true) {
                List<OrderStatus> orderStatusList = orderStatusRepository.findByOrderProduct(orderProduct);
                
                if(orderStatusList.size() == 0)
                    throw new BadRequestException("Invalid data format in order status for order product with id " + orderProduct.getOrderProductId() + " ");

                OrderStatus orderStatus = orderStatusList.get(0);
                FromStatus fromStatus = orderStatus.getFromStatus();
                if(fromStatus.name().equals("ORDER_PLACED")) {
                    orderStatus.setToStatus(ToStatus.CANCELLED);
                    orderStatus.setTransitionNotesComments("from " + orderStatus.getFromStatus() + " to " + orderStatus.getToStatus());

                    orderStatusRepository.save(orderStatus);

                    return "Your order product with ID" + orderProduct.getOrderProductId() + ", is cancelled by you";
                }
                else {
                    return "Can't cancel the order product with ID" + orderProduct.getOrderProductId() + " ";
                }
            }
            else {
                throw new BadRequestException("The order product with id " + orderProduct.getOrderProductId() + ", is not ordered by the customer " + customer.getFirstName());
            }
        }
        else {
            throw new ResourceNotFoundException("Invalid order product ID " + orderProductId + ", No order product found in the database");
        }
    }

    @Transactional
    public String orderReturn(Customer customer, Long orderProductId) {
        Optional<OrderProduct> orderProductOptional = orderProductRepository.findById(orderProductId);
        Boolean flag = false;

        if(orderProductOptional.isPresent()) {
            OrderProduct orderProduct = orderProductOptional.get();

            List<ConsolidatedOrder> consolidatedOrderList = consolidatedOrderRepository.findByCustomer(customer);

            if(consolidatedOrderList.size() == 0)
                throw new BadRequestException("No order is placed by the customer" + customer.getFirstName());

            for(ConsolidatedOrder consolidatedOrder : consolidatedOrderList) {
                List<OrderProduct> orderProductList = orderProductRepository.findByConsolidatedOrderId(consolidatedOrder.getOrderId());

                for(OrderProduct orderProductFetch : orderProductList) {
                    if(orderProductFetch.getOrderProductId().equals(orderProduct.getOrderProductId())) {
                        flag = true;
                        break;
                    }
                }
            }

            if(flag == true) {
                List<OrderStatus> orderStatusList = orderStatusRepository.findByOrderProduct(orderProduct);

                if(orderStatusList.size() == 0)
                    throw new BadRequestException("Invalid data format in order status for order product with id " + orderProduct.getOrderProductId() + " ");

                OrderStatus orderStatus = orderStatusList.get(0);
                FromStatus fromStatus = orderStatus.getFromStatus();
                if(fromStatus.name().equals("DELIVERED")) {
                    orderStatus.setToStatus(ToStatus.RETURN_REQUESTED);
                    orderStatus.setTransitionNotesComments("from " + orderStatus.getFromStatus() + " to " + orderStatus.getToStatus());

                    orderStatusRepository.save(orderStatus);

                    return "Your order product with ID" + orderProduct.getOrderProductId() + ", is successfully requested for a return by you";
                }
                else {
                    return "Can't request for a return the order product with ID" + orderProduct.getOrderProductId() + " ";
                }
            }
            else {
                throw new BadRequestException("The order product with id " + orderProduct.getOrderProductId() + ", is not ordered by the customer " + customer.getFirstName());
            }
        }
        else {
            throw new ResourceNotFoundException("Invalid order product ID " + orderProductId + ", No order product found in the database");
        }
    }


    public OrderViewModel viewOrder(Customer customer, Long orderId) {
        List<Optional<ConsolidatedOrder>> optionalOrderList = consolidatedOrderRepository.findByOrderIdAndCustomerId(orderId, customer.getUserId());
        if (optionalOrderList.size() == 0)
            throw new ResourceNotFoundException("Invalid order ID, no order found with the order ID " + orderId + " for the customer " + customer.getFirstName() + " ");

        if (optionalOrderList.size() == 2)
            throw new BadRequestException("Invalid data format for order in the database with the order ID " + orderId + " for the customer " + customer.getFirstName() + " ");

        Optional<ConsolidatedOrder> orderOptional = optionalOrderList.get(0);
        if (orderOptional.isPresent()) {
            OrderViewModel orderViewModel = new OrderViewModel();
            ConsolidatedOrder consolidatedOrder = orderOptional.get();

            List<OrderProduct> orderProductList = orderProductRepository.findByConsolidatedOrderId(consolidatedOrder.getOrderId());

            orderViewModel.setConsolidatedOrder(consolidatedOrder);
            orderViewModel.setOrderProductList(orderProductList);

            return orderViewModel;

        } else {
            throw new ResourceNotFoundException("Invalid order ID, no order found with the order ID " + orderId + " for the customer " + customer.getFirstName() + " ");
        }
    }

    public List<OrderViewModel> viewAllOrders(Customer customer, String page, String size) {
        List<ConsolidatedOrder> orderList = consolidatedOrderRepository.findByCustomer(customer);

        if (orderList.size() == 0)
            throw new BadRequestException("There is no order placed by the customer " + customer.getFirstName() + ", Please order to get the details about all the orders");

        List<OrderViewModel> orderViewModelList = new ArrayList<>();

        for (ConsolidatedOrder order : orderList) {
            List<OrderProduct> orderProductList = orderProductRepository.findByConsolidatedOrderId(order.getOrderId());

            OrderViewModel orderViewModel = new OrderViewModel();
            orderViewModel.setConsolidatedOrder(order);
            orderViewModel.setOrderProductList(orderProductList);

            orderViewModelList.add(orderViewModel);
        }

        return orderViewModelList;
    }

    @Transactional
    public String deleteAllOrders(Customer customer) {
        List<ConsolidatedOrder> orderList = consolidatedOrderRepository.findByCustomer(customer);

        if (orderList.size() == 0)
            throw new BadRequestException("There is no order placed by the customer " + customer.getFirstName() + ", Please order to get the details about all the orders");

        for(ConsolidatedOrder order : orderList) {
            orderProductRepository.deleteAllProductOrder(order.getOrderId());

            consolidatedOrderRepository.deleteAllConsolidatedOrder(customer.getUserId(),order.getOrderId());
        }

        return "All orders for customer " + customer.getFirstName() + " gets deleted";
    }

    public List<SellerViewOrderModel> sellerViewAllOrders(Seller seller, String page, String size) {
        List<Product> productList = productRepository.findSellerAssociatedProducts(seller.getUserId());
        if(productList.size() == 0)
            throw new BadRequestException("No product is listed by the seller " + seller.getFirstName());

        List<OrderProduct> sellerOrderProductList = new ArrayList<>();
        List<SellerViewOrderModel> sellerViewOrderModelList = new ArrayList<>();

        for(Product product : productList) {
            if(!product.getDeleted()) {
                List<ProductVariation> productVariationList = productVariationRepository.findAllProductVariationWithProductId(product.getProductId());
                for(ProductVariation productVariation : productVariationList) {
                    List<OrderProduct> orderProductList = orderProductRepository.findByProductVariation(productVariation);

                    if(orderProductList.size() != 0) {
                        sellerOrderProductList.addAll(orderProductList);
                    }
                }
            }
        }

        for(OrderProduct orderProduct : sellerOrderProductList) {
            SellerViewOrderModel sellerViewOrderModel = new SellerViewOrderModel();

            sellerViewOrderModel.setOrderProduct(orderProduct);
            List<OrderStatus> orderStatusList = orderStatusRepository.findByOrderProduct(orderProduct);

            if(orderStatusList.size() == 0)
                throw new BadRequestException("Invalid data format is saved for " + orderProduct.getProductVariation().getVariantName() + " in the order status");

            OrderStatus orderStatus = orderStatusList.get(0);
           sellerViewOrderModel.setOrderStatus(orderStatus);

            sellerViewOrderModelList.add(sellerViewOrderModel);
        }

        return sellerViewOrderModelList;
    }

    @Transactional
    public String sellerUpdateOrderStatus(Seller seller, Long orderProductId, String fromStatus, String toStatus) {
        Optional<OrderProduct> orderProductOptional = orderProductRepository.findById(orderProductId);

        Boolean flag = false;

        if(orderProductOptional.isPresent()) {
            OrderProduct orderProduct = orderProductOptional.get();

            List<Product> productList = productRepository.findSellerAssociatedProducts(seller.getUserId());

            if(productList.size() == 0) throw new BadRequestException("No product is listed by the seller " + seller.getFirstName() + " ");

            for(Product product : productList) {
                List<ProductVariation> productVariationList = productVariationRepository.findAllProductVariationWithProductId(product.getProductId());

                if(productVariationList.size() != 0) {
                    for(ProductVariation productVariation : productVariationList) {
                        if (productVariation.getProductVariationId().equals(orderProduct.getProductVariation().getProductVariationId())) {
                            flag = true;
                            break;
                        }
                    }
                }
            }

            if(flag == true) {
                try {
                    FromStatus fStatus = FromStatus.valueOf(fromStatus);
                    ToStatus tStatus = ToStatus.valueOf(toStatus);

                    List<OrderStatus> orderStatusList = orderStatusRepository.findByOrderProduct(orderProduct);

                    if(orderStatusList.size() == 0)
                        throw new BadRequestException("Invalid data format for order status for order product with ID " + orderProduct.getOrderProductId() + " ");

                    OrderStatus orderStatus = orderStatusList.get(0);
                    orderStatus.setFromStatus(fStatus);
                    orderStatus.setToStatus(tStatus);
                    orderStatus.setTransitionNotesComments("from " + orderStatus.getFromStatus() + " to " + orderStatus.getToStatus());

                    orderStatusRepository.save(orderStatus);

                    if(toStatus.equals("ORDER_CONFIRMED")) {
                        ProductVariation productVariation = orderProduct.getProductVariation();
                        productVariation.setQuantityAvailable(productVariation.getQuantityAvailable() - 1);

                        productVariationRepository.save(productVariation);
                    }

                    return "Order status gets updated for the order product with id " + orderProductId + " by the seller";
                }
                catch (IllegalArgumentException e) {
                    throw new BadRequestException("Invalid FromStatus or ToStatus format");
                }
            }
            else {
                throw new BadRequestException("The order product with ID " + orderProductId + ", does not belong to the seller " + seller.getFirstName() + " ");
            }
        }
        else {
            throw new ResourceNotFoundException("Invalid order product ID " + orderProductId + ", No order product found in the database");
        }
    }

    public List<AdminViewOrderModel> adminViewAllOrders(String page, String size) {
        List<AdminViewOrderModel> adminViewOrderModelList = new ArrayList<>();

        List<OrderProduct> orderProductList = orderProductRepository.findAll();

        for(OrderProduct orderProduct : orderProductList) {
            AdminViewOrderModel adminViewOrderModel = new AdminViewOrderModel();
            adminViewOrderModel.setOrderProduct(orderProduct);

            List<OrderStatus> orderStatusList = orderStatusRepository.findByOrderProduct(orderProduct);

            if(orderProductList.size() == 0)
                throw new BadRequestException("Invalid data format in order status for the order product with ID " + orderProduct.getOrderProductId() + " ");

            OrderStatus orderStatus = orderStatusList.get(0);
            adminViewOrderModel.setOrderStatus(orderStatus);

            adminViewOrderModelList.add(adminViewOrderModel);
        }

        return adminViewOrderModelList;
    }

    public String adminUpdateOrderStatus(Long orderProductId, String fromStatus, String toStatus) {
        Optional<OrderProduct> orderProductOptional = orderProductRepository.findById(orderProductId);

        if(orderProductOptional.isPresent()) {
            OrderProduct orderProduct = orderProductOptional.get();

            try {
                FromStatus fStatus = FromStatus.valueOf(fromStatus);
                ToStatus tStatus = ToStatus.valueOf(toStatus);

                List<OrderStatus> orderStatusList = orderStatusRepository.findByOrderProduct(orderProduct);

                if(orderStatusList.size() == 0)
                    throw new BadRequestException("Invalid data format for order status for order product with ID " + orderProduct.getOrderProductId() + " ");

                OrderStatus orderStatus = orderStatusList.get(0);
                orderStatus.setFromStatus(fStatus);
                orderStatus.setToStatus(tStatus);
                orderStatus.setTransitionNotesComments("from " + orderStatus.getFromStatus() + " to " + orderStatus.getToStatus());

                orderStatusRepository.save(orderStatus);

                return "Order status gets updated for the order product with id " + orderProductId + " by the admin";
            }
            catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid FromStatus or ToStatus format");
            }

        }
        else {
            throw new ResourceNotFoundException("Invalid order product ID " + orderProductId + ", No order product found in the database");
        }
    }
}
