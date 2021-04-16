package com.ShopOnline.Buy.online.services;

import com.ShopOnline.Buy.online.entities.Customer;
import com.ShopOnline.Buy.online.entities.order.Cart;
import com.ShopOnline.Buy.online.entities.product.Product;
import com.ShopOnline.Buy.online.entities.product.ProductVariation;
import com.ShopOnline.Buy.online.exceptions.BadRequestException;
import com.ShopOnline.Buy.online.exceptions.ResourceNotFoundException;
import com.ShopOnline.Buy.online.models.CartViewModel;
import com.ShopOnline.Buy.online.repos.CartRepository;
import com.ShopOnline.Buy.online.repos.ProductVariationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartDaoService {

    @Autowired
    CartRepository cartRepository;
    @Autowired
    ProductVariationRepository productVariationRepository;

    @Transactional
    public String addProductToCart(Long productVariationId, Integer quantity, Customer customer) {
        Optional<ProductVariation> productVariationOptional = productVariationRepository.findById(productVariationId);
        if(productVariationOptional.isPresent()) {
            ProductVariation productVariation = productVariationOptional.get();
            Product product = productVariation.getProduct();
            if(!product.getDeleted()) {
                if(productVariation.getActive()) {
                    if(quantity > 0) {
                        List<Optional<Cart>> optionalCartList = cartRepository.findByCustomerAndProductVariationId(customer.getUserId(), productVariationId);

                        if(optionalCartList.size() >= 2) throw new BadRequestException("Multiple entries found for the same product variation for the same customer");

                        if(optionalCartList.size() != 0) {
                            Optional<Cart> cartOptional = optionalCartList.get(0);

                            if(cartOptional.isPresent()) {
                                Cart cart = cartOptional.get();
                                cart.setQuantity(cart.getQuantity() + quantity);

                                cartRepository.save(cart);

                                return productVariation.getVariantName() + " gets successfully added in the cart and the quantity gets updated in the cart ";
                            }
                            else {
                                throw new BadRequestException("No data found for the logged in customer " + customer.getUsername()+ " with " + productVariation.getVariantName() + " in the database " + " ");
                            }
                        }
                        else {
                            Cart cart = new Cart();
                            cart.setCustomer(customer);
                            cart.setProductVariation(productVariation);
                            cart.setQuantity(quantity);

                            cartRepository.save(cart);

                            return productVariation.getVariantName() + " gets successfully added in the cart";
                        }
                    }
                    else {
                        throw new BadRequestException("Quantity can not be zero");
                    }
                }
                else {
                    throw new BadRequestException("Can't add the " + productVariation.getVariantName() + " as it is not in active state");
                }
            }
            else {
                throw new BadRequestException("Can't purchase " + productVariation.getVariantName() + " as the product " + product.getProductName() + " is deleted by the seller");
            }
        }
        else {
            throw new ResourceNotFoundException("Invalid product variation id, No product variation found with the id " + productVariationId + " ");
        }
    }

    @Transactional
    public List<CartViewModel> getAllProductsVariationForCustomer(Customer customer) {
        List<Long> productVariationIdList = cartRepository.findProductVariationId(customer.getUserId());
        List<CartViewModel> cartViewModelList = new ArrayList<>();

        if(productVariationIdList.size() != 0) {
            for(Long id : productVariationIdList) {
                CartViewModel cartViewModel = new CartViewModel();
                ProductVariation productVariation = productVariationRepository.findById(id).get();

                cartViewModel.setProductVariation(productVariation);
                Integer quantity = cartRepository.findByProductVariation(productVariation).get().getQuantity();
                cartViewModel.setOrderedQuantity(quantity);
                cartViewModel.setAvailableQuantity(productVariation.getQuantityAvailable());
                cartViewModel.setOutOfStock(false);
                cartViewModel.setDescription("The " + productVariation.getVariantName() + " is in the stock");

                Integer productVariationAvailableQuantity = productVariation.getQuantityAvailable();

                if(quantity > productVariationAvailableQuantity) {
                    cartViewModel.setOutOfStock(true);
                    cartViewModel.setAvailableQuantity(productVariationAvailableQuantity);
                    cartViewModel.setDescription(quantity + " quantity for " + productVariation.getVariantName() + " is not in the stock as the current available quantity to order is " + productVariationAvailableQuantity + " by the seller");
                }

                cartViewModelList.add(cartViewModel);
            }

            return cartViewModelList;
        }
        else {
            return cartViewModelList;
        }
    }

    @Transactional
    public String deleteProductFromCart(Long productVariationId, Customer customer) {
        Optional<ProductVariation> productVariationOptional = productVariationRepository.findById(productVariationId);
        if(productVariationOptional.isPresent()) {
            ProductVariation productVariation = productVariationOptional.get();
            List<Optional<Cart>> optionalCartList = cartRepository.findByCustomerAndProductVariationId(customer.getUserId(), productVariationId);

            if(optionalCartList.size() >= 2) throw new BadRequestException("Multiple entries found for the same product variation for the same customer");

            if(optionalCartList.size() != 0 ) {
                Optional<Cart> cartOptional = optionalCartList.get(0);
                if(cartOptional.isPresent()) {
                    Cart cart = cartOptional.get();

                    cartRepository.delete(cart);

                    return productVariation.getVariantName() + " successfully deleted from the cart";
                }
                else {
                    throw new BadRequestException("No data found for the logged in customer " + customer.getUsername()+ " with " + productVariation.getVariantName() + " in the database " + " ");
                }
            }
            else {
                throw new BadRequestException("No " + productVariation.getVariantName()+ " is added by the customer " + customer.getUsername() + " in the cart");
            }
        }
        else {
            throw new ResourceNotFoundException("Invalid product variation id, No product variation found with the id " + productVariationId + " ");
        }
    }

    @Transactional
    public CartViewModel updateCart(Long productVariationId, Integer quantity, Customer customer) {
        Optional<ProductVariation> productVariationOptional = productVariationRepository.findById(productVariationId);
        if(productVariationOptional.isPresent()) {
            ProductVariation productVariation = productVariationOptional.get();
            Product product = productVariation.getProduct();
            if(!product.getDeleted()) {
                if(productVariation.getActive()) {
                    List<Optional<Cart>> optionalCartList = cartRepository.findByCustomerAndProductVariationId(customer.getUserId(), productVariationId);

                    if(optionalCartList.size() == 0) throw new BadRequestException("No product variation added by the customer " + customer.getUsername() + " as nothing found in the cart with id " + productVariationId + " ");

                    if(optionalCartList.size() >= 2 ) throw new BadRequestException("Multiple entries found for the same product variation for the same customer");

                    if(quantity >= 0) {
                        CartViewModel cartViewModel = new CartViewModel();
                        Optional<Cart> cartOptional = optionalCartList.get(0);
                        Cart cart = cartOptional.get();

                        if(quantity == 0) {
                            cartRepository.delete(cart);

                            cartViewModel.setDescription("All quantity for the " + productVariation.getVariantName() + " is deleted from the cart");

                            return cartViewModel;
                        }
                        else {
                            cart.setQuantity(quantity);
                            cartViewModel.setProductVariation(productVariation);
                            cartViewModel.setOrderedQuantity(quantity);
                            cartViewModel.setAvailableQuantity(productVariation.getQuantityAvailable());
                            cartViewModel.setOutOfStock(false);
                            cartViewModel.setDescription("The " + productVariation.getVariantName() + " is in the stock");

                            if(quantity > productVariation.getQuantityAvailable()) {
                                cartViewModel.setOutOfStock(true);
                                cartViewModel.setDescription(quantity + " quantity for " + productVariation.getVariantName() + " is not in the stock as the current available quantity to order is " + productVariation.getQuantityAvailable() + " by the seller");
                            }

                            cartRepository.save(cart);

                            return cartViewModel;
                        }
                    }
                    else {
                        throw new BadRequestException("The quantity should be equal or greater than 0");
                    }
                }
                else {
                    throw new BadRequestException("Can't update the " + productVariation.getVariantName() + " as it is not in the active state");
                }
            }
            else {
                throw new BadRequestException("Can't update " + productVariation.getVariantName() + " as the product " + product.getProductName() + " is deleted by the seller");
            }
        }
        else {
            throw new ResourceNotFoundException("Invalid product variation id, No product variation found with the id " + productVariationId + " ");
        }
    }

    public String emptyCart(Customer customer) {
        List<Cart> cartList = cartRepository.findByCustomer(customer);
        if(cartList.size() != 0) {
            cartRepository.deleteAll(cartList);

            return "All products gets deleted, Cart is empty now";
        }
        else {
            return "Cart is already empty";
        }
    }

}
