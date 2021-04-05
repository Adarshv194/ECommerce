package com.ShopOnline.Buy.online.services;

import com.ShopOnline.Buy.online.entities.Seller;
import com.ShopOnline.Buy.online.entities.category.Category;
import com.ShopOnline.Buy.online.entities.category.CategoryMetaDataField;
import com.ShopOnline.Buy.online.entities.product.Product;
import com.ShopOnline.Buy.online.entities.product.ProductVariation;
import com.ShopOnline.Buy.online.exceptions.BadRequestException;
import com.ShopOnline.Buy.online.exceptions.ResourceNotFoundException;
import com.ShopOnline.Buy.online.models.ProductModel;
import com.ShopOnline.Buy.online.models.ProductUpdateModel;
import com.ShopOnline.Buy.online.models.ProductUpdateVariationModel;
import com.ShopOnline.Buy.online.models.ProductVariationModel;
import com.ShopOnline.Buy.online.repos.CategoryMetadataFieldRepository;
import com.ShopOnline.Buy.online.repos.CategoryRepository;
import com.ShopOnline.Buy.online.repos.ProductRepository;
import com.ShopOnline.Buy.online.repos.ProductVariationRepository;
import com.ShopOnline.Buy.online.utils.HashMapCoverter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProductDaoService {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    EmailSenderService emailSenderService;
    @Autowired
    ProductVariationRepository productVariationRepository;
    @Autowired
    CategoryMetadataFieldRepository categoryMetadataFieldRepository;

    public String addProduct(String categoryName, Seller seller, ProductModel productModel) {
        Optional<Category> categoryOptional = categoryRepository.findByName(categoryName);

        if(categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            if(category.getParentCategory() == null) {
                throw new BadRequestException("Product should be the leaf category, " + categoryName + " is not a leaf category");
            }
            else {
                ModelMapper mapper = new ModelMapper();
                Product product = mapper.map(productModel, Product.class);

                String productName = productRepository.checkForUniqueness(product.getProductName(),product.getBrand(), category.getCategoryId(), seller.getUserId());

                if(product.getProductName().equals(productName)) {
                    throw new BadRequestException("Can't add the same product again, don't use the " + productName + " product to add as you had already saved it");
                }
                else {
                    product.setSeller(seller);
                    product.setCategory(category);
                    product.setCancellable(false);
                    product.setReturnable(false);
                    product.setActive(false);

                    productRepository.save(product);

                    SimpleMailMessage mailMessage = new SimpleMailMessage();
                    mailMessage.setTo("adarsh.verma@tothenew.com");
                    mailMessage.setFrom("adarshv193@gmail.com");
                    mailMessage.setSubject("A new product has been added by the seller");
                    mailMessage.setText("A new product has been added by the seller, Please manage");

                    emailSenderService.sendEmail(mailMessage);

                    return "Product added successfully";
                }
            }
        }
        else {
            throw new ResourceNotFoundException("Invalid Category name, Category not found in the database with category name " + categoryName + " " );
        }
    }

    public String addProductVariation(Long productId, Seller seller, ProductVariationModel productVariationModel) {
        Optional<Product> productOptional = productRepository.findById(productId);

        if(productOptional.isPresent()) {
            Product product = productOptional.get();

            if(product.getActive() == false) {
                throw new BadRequestException("Can't add the product variations as the product is not active by the Admin ");
            }

            if(product.getDeleted() == true) {
                throw new BadRequestException("Can't add the product variation as the product is deleted ");
            }

            ModelMapper mapper = new ModelMapper();
            ProductVariation productVariation = mapper.map(productVariationModel, ProductVariation.class);

            if(product.getSeller().getUserId().equals(seller.getUserId())) {

                List<String> allProductVariationAttributes = productVariationRepository.findAllProductVariationAttributes(productId);

                if(allProductVariationAttributes.size() != 0) {

                    HashMapCoverter hashMapCoverter = new HashMapCoverter();

                    for(String productAttributes : allProductVariationAttributes) {
                        Map<String, Object> map = hashMapCoverter.convertToEntityAttribute(productAttributes);
                        if(map.equals(productVariation.getProductAttributes())) {
                            throw new BadRequestException("Can't add the product variation with these product attributes , Please try some other varaition");
                        }
                    }

                    if(checkProductValidationModel(productId,productVariationModel)) {

                        if(productVariation.getQuantityAvailable() <= 0) {
                            throw new BadRequestException("Quantity should be greater than 0");
                        }
                        if(productVariation.getPrice() <= 0) {
                            throw new BadRequestException("Price should be greater than 0");
                        }

                        productVariation.setProduct(product);
                        productVariation.setActive(true);
                        productVariation.setDeleted(false);

                        productVariationRepository.save(productVariation);

                        return "Product Variant saved";
                    }
                    else {
                        return "Wrong category meta data field - values format ";
                    }
                }
                else {
                    if(checkProductValidationModel(productId,productVariationModel)) {

                        if(productVariation.getQuantityAvailable() <= 0) {
                            throw new BadRequestException("Quantity should be greater than 0");
                        }
                        if(productVariation.getPrice() <= 0) {
                            throw new BadRequestException("Price should be greater than 0");
                        }

                        productVariation.setProduct(product);
                        productVariation.setActive(true);
                        productVariation.setDeleted(false);

                        productVariationRepository.save(productVariation);

                        return "Product Variant saved";
                    }
                    else {
                        return "Wrong category meta data field - values format ";
                    }
                }
            }
            else {
                throw new BadRequestException("Product " + product.getProductName() + " is not associated with the logged in seller " + seller.getFirstName() + " ");
            }
        }
        else {
            throw new ResourceNotFoundException("Invalid product ID, product not found with ID " + productId + " ");
        }
    }

    public Boolean checkProductValidationModel(Long product_id, ProductVariationModel productVariationModel) {
        System.out.println("Calling check for checkProductValidationModel");

        Product product = productRepository.findById(product_id).get();

        Map<String, String> productAttributes = productVariationModel.getProductAttributes();

        Set<String> modelKeySets = productAttributes.keySet();

        System.out.println(modelKeySets);

        Integer modelMetadatLengthField = modelKeySets.size();

        if(modelMetadatLengthField <= 0) {
            throw new BadRequestException("There should atleast one metadata field values, and all the product variations should have the same format");
        }

        System.out.println(modelMetadatLengthField);

        List<Long> categoryMetaDataFieldsIDs = productVariationRepository.checkDbMetadataLengthField(product.getCategory().getCategoryId());

        if(categoryMetaDataFieldsIDs.size() != modelMetadatLengthField) {
            throw new BadRequestException("The product variation category metadata field values does not match the structure");
        }

        Set<String> categoryMetaDataFieldListName = new HashSet<>();

        System.out.println("All good here");

        System.out.println(categoryMetaDataFieldsIDs);

        categoryMetaDataFieldsIDs.forEach(id -> {
            System.out.println(id);
            String name = categoryMetadataFieldRepository.findById(id).get().getName();
            System.out.println(name);
            categoryMetaDataFieldListName.add(name);
        });

        System.out.println("All good here 1");

        for(String field : modelKeySets) {
            Boolean checker = false;
            for(String dbField : categoryMetaDataFieldListName) {
                if(field.equals(dbField)) {
                 checker = true;
                }
            }
            if(checker == false) {
                throw new BadRequestException("Wrong metadata field name is inserted with name " + field + " ");
            }
        }

        return true;
    }

    public String updateProduct(Long productId, ProductUpdateModel productModel, Seller seller) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if(productOptional.isPresent()) {
            Product product = productOptional.get();

            if(!seller.getUserId().equals(product.getSeller().getUserId())) {
                throw new BadRequestException("Can't update this product as this product does not get listed by you");
            }

            if(product.getActive() == false) {
                throw new BadRequestException("Can't update the " + product.getProductName() + "as the product is not activated by the Admin");
            }

            if(product.getDeleted() == true) {
                throw new BadRequestException("Can't update the " + product.getProductName() + "as the product is deleted");
            }

            if(productModel.getProductName() != null) {
                String productName = productModel.getProductName();
                String checkProductName = productRepository.checkForUniqueness(productName,product.getBrand(),product.getCategory().getCategoryId(),seller.getUserId());

                if(productName.equals(checkProductName)) {
                    throw new BadRequestException("As there is another product listed by you with name " + checkProductName + " can't update, Please try some other name");
                }

                product.setProductName(productModel.getProductName());
            }

            if(productModel.getProductDescription() != null) {
                product.setProductDescription(productModel.getProductDescription());
            }

            if(productModel.getCancellable() != null) {
                product.setCancellable(productModel.getCancellable());
            }

            if(productModel.getReturnable() != null) {
                product.setReturnable(productModel.getReturnable());
            }

            return "Product updated successfully";
        }
        else {
            throw new BadRequestException("Invalid product Id, Product not found with the id " + productId + " ");
        }
    }

    public String updateProductVariaiton(Long productVariationId, ProductUpdateVariationModel productVariationModel, Seller seller) {
        Optional<ProductVariation> productVariationOptional = productVariationRepository.findById(productVariationId);
        if(productVariationOptional.isPresent()) {
            ProductVariation productVariation = productVariationOptional.get();

            if(!productVariation.getProduct().getSeller().getUserId().equals(seller.getUserId())) {
                throw new BadRequestException("Can't update this product variation as this product variation does not get listed by you");
            }

            if(productVariation.getDeleted() == true) {
                throw new BadRequestException("Can't update this product variation as this product variation is deleted");
            }

            if(productVariation.getActive() == false) {
                throw new BadRequestException("Can't update the " + productVariation.getVariantName() + "as the product is not activated by the Admin");
            }

            if(productVariationModel.getVariantName() != null) {
                List<ProductVariation> productVariationList = productVariationRepository.checkForProductvariationWithNameAndProductId(productVariationModel.getVariantName(), productVariationId);
                if(productVariationList.size() != 0) {
                    throw new BadRequestException("Can't update the product variation as there exist a product variation with variation name " + productVariationModel.getVariantName() + " ");
                }

                productVariation.setVariantName(productVariationModel.getVariantName());
            }

            if(productVariationModel.getProductAttributes() != null) {
                Map<String, Object> productAttributesClient = productVariationModel.getProductAttributes();

                List<String> allProductVariationAttributes = productVariationRepository.findAllProductVariationAttributes(productVariation.getProduct().getProductId());

                HashMapCoverter hashMapCoverter = new HashMapCoverter();

                for(String productAttributes : allProductVariationAttributes) {
                    Map<String, Object> map = hashMapCoverter.convertToEntityAttribute(productAttributes);
                    if(map.equals(productVariation.getProductAttributes())) {
                        throw new BadRequestException("Can't update the product variation with these product attributes , Please try some other varaition");
                    }
                }

                if(checkProductValidationModel(productVariation.getProduct().getProductId(),productVariationModel)) {
                    productVariation.setProductAttributes(productVariationModel.getProductAttributes());
                }
            }

            if
        }
        else {
            throw new ResourceNotFoundException("Invalid product variation id, No record found with the product variation id " + productVariationId + " ");
        }
    }

    public Boolean checkProductValidationModel(Long productId, ProductUpdateVariationModel productUpdateVariationModel) {
        Product product = productRepository.findById(productId).get();

        Map<String, Object> productAttributes = productUpdateVariationModel.getProductAttributes();
        Set<String> modelKeySet = productAttributes.keySet();

        if (modelKeySet.size() <= 0) {
            throw new BadRequestException("There should atleast one metadata field values, and all the product variations should have the same format");
        }

        List<Long> categoryMetaDataFieldsIDs = productVariationRepository.checkDbMetadataLengthField(product.getCategory().getCategoryId());

        if(modelKeySet.size() != categoryMetaDataFieldsIDs.size()) {
            throw new BadRequestException("The product variation category metadata field values does not match the structure");
        }

        Set<String> categoryMetaDataFieldListName = new HashSet<>();

        categoryMetaDataFieldsIDs.forEach(id -> {
            categoryMetaDataFieldListName.add(categoryMetadataFieldRepository.findById(id).get().getName());
        });

        for(String field : modelKeySet) {
            Boolean checker = false;
            for(String dbField : categoryMetaDataFieldListName) {
                if(field.equals(dbField)) {
                    checker = true;
                }
            }
            if(checker == false) {
                throw new BadRequestException("Wrong metadata field name is inserted with name " + field + " ");
            }
        }

        return true;
    }

}
