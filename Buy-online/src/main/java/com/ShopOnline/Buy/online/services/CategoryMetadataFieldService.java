package com.ShopOnline.Buy.online.services;

import com.ShopOnline.Buy.online.entities.category.Category;
import com.ShopOnline.Buy.online.entities.category.CategoryMetaDataField;
import com.ShopOnline.Buy.online.entities.category.CategoryMetaDataFieldValues;
import com.ShopOnline.Buy.online.exceptions.BadRequestException;
import com.ShopOnline.Buy.online.exceptions.ResourceNotFoundException;
import com.ShopOnline.Buy.online.repos.CategoryMetadataFieldRepository;
import com.ShopOnline.Buy.online.repos.CategoryMetadataFieldValuesRepository;
import com.ShopOnline.Buy.online.repos.CategoryRepository;
import com.ShopOnline.Buy.online.utils.StringToSetParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CategoryMetadataFieldService {

    @Autowired
    CategoryMetadataFieldRepository categoryMetadataFieldRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    CategoryMetadataFieldValuesRepository categoryMetadataFieldValuesRepository;

    public String addMetadataField(String field) {
        Optional<CategoryMetaDataField> categoryMetaDataFieldOptional = categoryMetadataFieldRepository.findByName(field);
        if(categoryMetaDataFieldOptional.isPresent()) {
            throw new ResourceNotFoundException("Category metadata field already exist with name " + field + " ");
        }
        else {
            CategoryMetaDataField categoryMetaDataField = new CategoryMetaDataField();
            categoryMetaDataField.setName(field);

            categoryMetadataFieldRepository.save(categoryMetaDataField);

            return "Category metadata field saved with name " + field + " and id " + categoryMetaDataField.getCategoryMetaDataFieldId() +" ";
        }
    }

    public String updateMetaDataField(Long fieldId, String fieldName) {
        Optional<CategoryMetaDataField> metaDataFieldOptional = categoryMetadataFieldRepository.findById(fieldId);
        if(metaDataFieldOptional.isPresent()) {
            CategoryMetaDataField categoryMetaDataField = metaDataFieldOptional.get();
            categoryMetaDataField.setName(fieldName);

            categoryMetadataFieldRepository.save(categoryMetaDataField);

            return "Metadata field gets updated successfully";
        }
        else {
            throw new ResourceNotFoundException("Invalid metadata field ID, no record found with id " + fieldId + " ");
        }
    }

    public List<CategoryMetaDataField> findAllCategoryMetaDataFields(String page, String size) {
        Pageable pageable = PageRequest.of(Integer.parseInt(page),Integer.parseInt(size));

        return categoryMetadataFieldRepository.findAll(pageable);
    }

    public String addNewCategoryMetadataFieldvalues(Long categoryId, Long metadataFieldId, Set<String> fieldValues) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        Optional<CategoryMetaDataField> categoryMetaDataFieldOptional = categoryMetadataFieldRepository.findById(metadataFieldId);

        if(!categoryOptional.isPresent()) {
            throw new ResourceNotFoundException("Invalid category ID, Category not found with the ID " + categoryId + " ");
        }
        if(!categoryMetaDataFieldOptional.isPresent()) {
            throw new ResourceNotFoundException("Invalid metadata field id, Metadata field not found wiht the ID " + metadataFieldId + " ");
        }
        else {
            Category category = categoryOptional.get();
            CategoryMetaDataField categoryMetaDataField = categoryMetaDataFieldOptional.get();

            if(category.getParentCategory() == null) {
                throw new BadRequestException("Can't add the field values to the parent category");
            }

            if(fieldValues.size() == 0) {
                throw new BadRequestException("Field values should contains atleast one value- pair");
            }

            CategoryMetaDataFieldValues categoryMetaDataFieldValues = new CategoryMetaDataFieldValues();
            categoryMetaDataFieldValues.setCategory(category);
            categoryMetaDataFieldValues.setCategoryMetaDataField(categoryMetaDataField);

            String values = StringToSetParser.toCommaSeperatedString(fieldValues);
            categoryMetaDataFieldValues.setFieldvalues(values);

            categoryMetadataFieldValuesRepository.save(categoryMetaDataFieldValues);

            return "Category metadata field values added successfully";
        }
    }

    public String updateMetadataFieldValues(Long categoryId, Long metadataFieldId, Set<String> fieldValues) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        Optional<CategoryMetaDataField> categoryMetaDataFieldOptional = categoryMetadataFieldRepository.findById(metadataFieldId);

        if(!categoryOptional.isPresent()) {
            throw new ResourceNotFoundException("Invalid category ID, Category not found with the ID " + categoryId + " ");
        }
        if(!categoryMetaDataFieldOptional.isPresent()) {
            throw new ResourceNotFoundException("Invalid metadata field id, Metadata field not found wiht the ID " + metadataFieldId + " ");
        }

        Category category = categoryOptional.get();
        CategoryMetaDataField categoryMetaDataField = categoryMetaDataFieldOptional.get();

        if(category.getParentCategory() == null) {
            throw new BadRequestException("Can't add the field values to the parent category");
        }

        if(fieldValues.size() == 0) {
            throw new BadRequestException("Field values should contains atleast one value- pair");
        }

        Optional<CategoryMetaDataFieldValues> categoryMetadataFieldValueOptional = categoryMetadataFieldValuesRepository.findByCategoryAndCategoryMetadataField(categoryId, metadataFieldId);

        if(categoryMetadataFieldValueOptional.isPresent()) {
            CategoryMetaDataFieldValues categoryMetaDataFieldValues = categoryMetadataFieldValueOptional.get();

            String values = StringToSetParser.toCommaSeperatedString(fieldValues);
            categoryMetaDataFieldValues.setFieldvalues(values);

            categoryMetadataFieldValuesRepository.save(categoryMetaDataFieldValues);

            return "Category metadata field values gets updated";
        }
        else {
            throw new ResourceNotFoundException("Category metadata field value does not found with the ID'S provided");
        }
    }

    public List<CategoryMetaDataFieldValues> getAllMetadataFieldValues(String page, String size) {
        Pageable pageable = PageRequest.of(Integer.parseInt(page),Integer.parseInt(size));

        return categoryMetadataFieldValuesRepository.findAll(pageable);
    }

}
