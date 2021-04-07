package com.ShopOnline.Buy.online.controllers;

import com.ShopOnline.Buy.online.entities.category.Category;
import com.ShopOnline.Buy.online.entities.category.CategoryMetaDataField;
import com.ShopOnline.Buy.online.models.CategoryModel;
import com.ShopOnline.Buy.online.models.FilterCategoryModel;
import com.ShopOnline.Buy.online.services.CategoryDaoService;
import com.ShopOnline.Buy.online.services.CategoryMetadataFieldService;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
public class CategoryController {

    @Autowired
    CategoryDaoService categoryDaoService;
    @Autowired
    CategoryMetadataFieldService categoryMetadataFieldService;

    @PostMapping(value = "/add-parent-category")
    public ResponseEntity<Object> addParentCategory(@Valid @RequestBody CategoryModel categoryModel) {
        String message = categoryDaoService.addParentCategory(categoryModel);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PostMapping(value = "/add-category/{parentCategory}")
    public ResponseEntity<Object> addSubCategory(@PathVariable String parentCategory, @Valid @RequestBody List<CategoryModel> categoryModelList) {
        String message = categoryDaoService.addSubCategories(parentCategory, categoryModelList);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PutMapping(value = "/update-category/{categoryName}")
    public ResponseEntity<Object> updateCategory(@PathVariable String categoryName, @Valid @RequestBody CategoryModel categoryModel) {
        String message = categoryDaoService.updateCategory(categoryName, categoryModel);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping(value = "/get-all-categories")
    public MappingJacksonValue getAllCategory(@RequestHeader(defaultValue = "0") String page, @RequestHeader(defaultValue = "10") String size) {

        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("name", "categoryId");

        SimpleBeanPropertyFilter filter2 = SimpleBeanPropertyFilter.filterOutAllExcept("categoryMetaDataField","fieldvalues");

        FilterProvider filters = new SimpleFilterProvider().addFilter("categoryfilter", filter1)
                .addFilter("categorymdfv",filter2);

        MappingJacksonValue mapping = new MappingJacksonValue(categoryDaoService.getAllCategory(page, size));
        mapping.setFilters(filters);

        return mapping;
    }

    @GetMapping(value = "/get-category/{categoryId}")
    public MappingJacksonValue getCategory(@PathVariable Long categoryId) {

        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("name", "categoryId");

        SimpleBeanPropertyFilter filter2 = SimpleBeanPropertyFilter.filterOutAllExcept("categoryMetaDataField","fieldvalues");

        FilterProvider filters = new SimpleFilterProvider().addFilter("categoryfilter", filter1)
                .addFilter("categorymdfv",filter2);

        MappingJacksonValue mapping = new MappingJacksonValue(categoryDaoService.getCategory(categoryId));
        mapping.setFilters(filters);

        return mapping;
    }

    @PostMapping(value = "/add-metadata/fields")
    public ResponseEntity<Object> addMetadataField(@RequestParam("field") String field) {
        String message = categoryMetadataFieldService.addMetadataField(field);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PutMapping(value = "/update-metadata-field/{fieldId}/{fieldName}")
    public ResponseEntity<Object> updateMetadataField(@PathVariable Long fieldId, @PathVariable String fieldName) {
        String message = categoryMetadataFieldService.updateMetaDataField(fieldId, fieldName);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping(value = "/all-metadata-fileds")
    public MappingJacksonValue findAllCategoryMetadataField(@RequestHeader(defaultValue = "0") String page, @RequestHeader(defaultValue = "10") String size) {

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("name", "categoryMetaDataFieldId");

        FilterProvider filters = new SimpleFilterProvider().addFilter("categoryfilter", filter);

        MappingJacksonValue mapping = new MappingJacksonValue(categoryMetadataFieldService.findAllCategoryMetaDataFields(page, size));
        mapping.setFilters(filters);

        return mapping;
    }

    @PostMapping(value = "/add-metadata/field/values/{categoryId}/{metadataFieldId}")
    public ResponseEntity<Object> addMetadataFieldValues(@PathVariable Long categoryId, @PathVariable Long metadataFieldId, @RequestBody Set<String> fieldValues) {
        String message = categoryMetadataFieldService.addNewCategoryMetadataFieldvalues(categoryId, metadataFieldId, fieldValues);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @PutMapping(value = "/update-metadata/field/values/{categoryId}/{metadataFieldId}")
    public ResponseEntity<Object> updateMetadataFieldValues(@PathVariable Long categoryId, @PathVariable Long metadataFieldId, @RequestBody Set<String> fieldValues) {
        String message = categoryMetadataFieldService.updateMetadataFieldValues(categoryId, metadataFieldId, fieldValues);

        return new ResponseEntity<>(message, HttpStatus.CREATED);
    }

    @GetMapping(value = "/get-metadata-field-values/")
    public MappingJacksonValue getAllMetadataFieldValues(@RequestHeader(defaultValue = "0") String page, @RequestHeader(defaultValue = "10") String size) {

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("fieldvalues");

        FilterProvider filters = new SimpleFilterProvider().addFilter("categoryfilter", filter);

        MappingJacksonValue mapping = new MappingJacksonValue(categoryMetadataFieldService.getAllMetadataFieldValues(page, size));
        mapping.setFilters(filters);

        return mapping;
    }

    @GetMapping(value = "/seller/get-all-categories")
    public MappingJacksonValue sellerGetAllCategory() {

        SimpleBeanPropertyFilter filter1 = SimpleBeanPropertyFilter.filterOutAllExcept("name", "categoryId");

        SimpleBeanPropertyFilter filter2 = SimpleBeanPropertyFilter.filterOutAllExcept("categoryMetaDataField","fieldvalues");

        FilterProvider filters = new SimpleFilterProvider().addFilter("categoryfilter", filter1)
                .addFilter("categorymdfv",filter2);

        MappingJacksonValue mapping = new MappingJacksonValue(categoryDaoService.sellerGetAllCategory());
        mapping.setFilters(filters);

        return mapping;
    }

    @GetMapping("/customer/get-all-categories")
    public MappingJacksonValue getAllRootLevelCategories() {

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("name");

        FilterProvider filters = new SimpleFilterProvider().addFilter("categoryfilter", filter);

        MappingJacksonValue mapping = new MappingJacksonValue(categoryDaoService.getAllRootLevelCategories());
        mapping.setFilters(filters);

        return mapping;
    }

    @GetMapping("/customer/get-all-categories/{categoryId}")
    public MappingJacksonValue getAllSubCategoriesWithId(@PathVariable Long categoryId) {

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("name");

        FilterProvider filters = new SimpleFilterProvider().addFilter("categoryfilter", filter);

        MappingJacksonValue mapping = new MappingJacksonValue(categoryDaoService.getAllSubCategoriesWithId(categoryId));
        mapping.setFilters(filters);

        return mapping;
    }

    @GetMapping("/customer/get-category/{categoryId}")
    public MappingJacksonValue getAllFilterCategoryWithId(@PathVariable Long categoryId) {

        SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("name");

        FilterProvider filters = new SimpleFilterProvider().addFilter("categoryfilter", filter);

        MappingJacksonValue mapping = new MappingJacksonValue(categoryDaoService.getCategory(categoryId));
        mapping.setFilters(filters);

        return mapping;
    }

}
