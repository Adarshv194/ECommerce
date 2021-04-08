package com.ShopOnline.Buy.online.services;

import com.ShopOnline.Buy.online.entities.category.Category;
import com.ShopOnline.Buy.online.entities.category.CategoryMetaDataFieldValues;
import com.ShopOnline.Buy.online.exceptions.BadRequestException;
import com.ShopOnline.Buy.online.exceptions.ResourceNotFoundException;
import com.ShopOnline.Buy.online.models.CategoryModel;
import com.ShopOnline.Buy.online.models.CategoryViewModel;
import com.ShopOnline.Buy.online.models.FilterCategoryModel;
import com.ShopOnline.Buy.online.repos.CategoryMetadataFieldValuesRepository;
import com.ShopOnline.Buy.online.repos.CategoryRepository;
import com.ShopOnline.Buy.online.repos.ProductRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.*;
import java.util.logging.Filter;

@Service
public class CategoryDaoService {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryMetadataFieldValuesRepository categoryMetadataFieldValuesRepository;

    public String addParentCategory(CategoryModel categoryModel) {
        Optional<Category> categoryOptional = categoryRepository.findByName(categoryModel.getName());
        if(categoryOptional.isPresent()) {
            throw new BadRequestException("Category already present in the database with name" + categoryModel.getName() + " ");
        }
        else {
            ModelMapper mapper = new ModelMapper();
            Category category = mapper.map(categoryModel, Category.class);

            categoryRepository.save(category);

            return "New category added in the database with name " + categoryModel.getName() + " with id " + category.getCategoryId() + " ";
        }
    }

    public String addSubCategories(String parentCategory, List<CategoryModel> categoryModelList) {
        Optional<Category> categoryOptional = categoryRepository.findByName(parentCategory);
        if(categoryOptional.isPresent()) {
            Category pCategory = categoryOptional.get();

            Type listType = new TypeToken<List<Category>>(){}.getType();
            ModelMapper mapper = new ModelMapper();
            List<Category> categoryList = mapper.map(categoryModelList,listType);

            List<String> modelNames = new ArrayList<>();
            categoryList.forEach(category -> {
                modelNames.add(category.getName());
            });

            List<String> categoryNames = categoryRepository.checkForCategoryName(pCategory.getCategoryId());
            for(String categoryName : categoryNames) {

                for(String name : modelNames) {
                    if(categoryName.equals(name)) {
                        throw new BadRequestException("can't add the Sub-category " + name + " as it is already added in the database");
                    }
                }
            }

            categoryList.forEach(category -> category.setParentCategory(pCategory));

            categoryRepository.saveAll(categoryList);

            return "All sub-categories gets saved in the database";
        }
        else {
            throw new ResourceNotFoundException("Category not found in the database with name " + parentCategory + " ");
        }
    }

    public String updateCategory(String categoryName, CategoryModel categoryModel) {
        Optional<Category> categoryOptional = categoryRepository.findByName(categoryName);

        if(categoryOptional.isPresent()) {

            Optional<Category> categoryNameOptional = categoryRepository.findByName(categoryModel.getName());
            if(categoryNameOptional.isPresent()) {
                throw new BadRequestException("Can't update the category with name " + categoryModel.getName() + " as there is already a categroy with that name");
            }

            Category category = categoryOptional.get();

            category.setName(categoryModel.getName());

            categoryRepository.save(category);

            return categoryName + " Category gets updated";
        }
        else {
            throw new ResourceNotFoundException("Category not found in the database with name " + categoryName + " ");
        }
    }

    public List<FilterCategoryModel> getAllCategory(String page, String size) {
        Pageable pageable = PageRequest.of(Integer.parseInt(page),Integer.parseInt(size));

        List<Category> categoryList = categoryRepository.findAll(pageable);
        List<FilterCategoryModel> filterCategoryModelList = new ArrayList<>();

        for(Category toReturnable : categoryList) {
            List<CategoryMetaDataFieldValues> toReturnableFV = categoryMetadataFieldValuesRepository.findByCategoryId(toReturnable.getCategoryId());

            FilterCategoryModel filterCategoryModelObj = new FilterCategoryModel();

            filterCategoryModelObj.setCategory(toReturnable);
            filterCategoryModelObj.setCategoryMetaDataFieldValuesList(toReturnableFV);

            filterCategoryModelList.add(filterCategoryModelObj);
        }

        return filterCategoryModelList;
    }

    public List<FilterCategoryModel> sellerGetAllCategory() {
        List<Category> categoryList = categoryRepository.findAllCategories();
        List<FilterCategoryModel> filterCategoryModelList = new ArrayList<>();

        for(Category toReturnable : categoryList) {
            List<CategoryMetaDataFieldValues> toReturnableFV = categoryMetadataFieldValuesRepository.findByCategoryId(toReturnable.getCategoryId());

            FilterCategoryModel filterCategoryModelObj = new FilterCategoryModel();

            filterCategoryModelObj.setCategory(toReturnable);
            filterCategoryModelObj.setCategoryMetaDataFieldValuesList(toReturnableFV);

            filterCategoryModelList.add(filterCategoryModelObj);
        }

        return filterCategoryModelList;
    }

    public List<FilterCategoryModel> getCategory(Long categoryId) {

        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if(categoryOptional.isPresent()) {
            Category category = categoryOptional.get();

            List<Category> categoryList = new ArrayList<>();

            if(category.getParentCategory() == null) {
                categoryList.add(category);

                List<Category> subCategories = categoryRepository.getAllSubCategoriesWithId(category.getCategoryId());

                List<FilterCategoryModel> filterCategoryModelList = new ArrayList<>();

                    for(Category subCategory : subCategories) {
                        categoryList.add(subCategory);
                    }

                for(Category toReturnable : categoryList) {
                    List<CategoryMetaDataFieldValues> toReturnableFV = categoryMetadataFieldValuesRepository.findByCategoryId(toReturnable.getCategoryId());

                    FilterCategoryModel filterCategoryModelObj = new FilterCategoryModel();

                    filterCategoryModelObj.setCategory(toReturnable);
                    filterCategoryModelObj.setCategoryMetaDataFieldValuesList(toReturnableFV);

                    filterCategoryModelList.add(filterCategoryModelObj);
                }

                return filterCategoryModelList;
            }
            else {
                categoryList.add(category);

                List<Category> allParentCategoryList = categoryRepository.findAllParentCategoryWithSubCategoryId(category.getParentCategory().getCategoryId());
                List<FilterCategoryModel> filterCategoryModelList = new ArrayList<>();

                for(Category parentCategory : allParentCategoryList) {
                    categoryList.add(parentCategory);
                }

                for(Category toReturnable : categoryList) {
                    List<CategoryMetaDataFieldValues> toReturnableFV = categoryMetadataFieldValuesRepository.findByCategoryId(toReturnable.getCategoryId());

                    FilterCategoryModel filterCategoryModelObj = new FilterCategoryModel();

                    filterCategoryModelObj.setCategory(toReturnable);
                    filterCategoryModelObj.setCategoryMetaDataFieldValuesList(toReturnableFV);

                    filterCategoryModelList.add(filterCategoryModelObj);
                }

                return filterCategoryModelList;
            }
        }
        else {
            throw new ResourceNotFoundException("Category not found with the ID " + categoryId + " ");
        }
    }

    public List<FilterCategoryModel> getAllRootLevelCategories() {

        List<Category> categoryList = categoryRepository.findRoot();
        List<FilterCategoryModel> filterCategoryModelList = new ArrayList<>();

        for(Category toReturnable : categoryList) {
            List<CategoryMetaDataFieldValues> toReturnableFV = categoryMetadataFieldValuesRepository.findByCategoryId(toReturnable.getCategoryId());

            FilterCategoryModel filterCategoryModelObj = new FilterCategoryModel();

            filterCategoryModelObj.setCategory(toReturnable);
            filterCategoryModelObj.setCategoryMetaDataFieldValuesList(toReturnableFV);

            filterCategoryModelList.add(filterCategoryModelObj);
        }

        return filterCategoryModelList;
    }

    public List<Category> getAllSubCategoriesWithId(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);

        if(categoryOptional.isPresent()) {
            return categoryRepository.getAllSubCategoriesWithId(categoryId);
        }
        else {
            throw new ResourceNotFoundException("Invalid category ID, no record found with the ID " + categoryId + " ");
        }
    }

    public List<CategoryViewModel> getAllFilterCategoryWithProducts(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if(categoryOptional.isPresent()) {
            Category category = categoryOptional.get();
            List<CategoryViewModel> categoryViewModelList = new ArrayList<>();
            List<Category> categoryList = new ArrayList<>();


            if(category.getParentCategory() == null) {
                System.out.println("called");
                List<Category> allParentCategoryList = categoryRepository.getAllSubCategoriesWithId(category.getCategoryId());

                for(Category parentCategory : allParentCategoryList) {
                    categoryList.add(parentCategory);
                    System.out.println(parentCategory.getName());
                }

                for(Category toReturnable : categoryList) {
                    List<CategoryMetaDataFieldValues> toReturnableFV = categoryMetadataFieldValuesRepository.findByCategoryId(toReturnable.getCategoryId());
                    List<String> allBrandName = productRepository.findAllBrandName(toReturnable.getCategoryId());

                    CategoryViewModel categoryViewModelObj = new CategoryViewModel();

                    categoryViewModelObj.setBrand(allBrandName);

                    categoryViewModelObj.setCategory(toReturnable);
                    categoryViewModelObj.setCategoryMetaDataFieldValuesList(toReturnableFV);

                    categoryViewModelList.add(categoryViewModelObj);
                }

                for (CategoryViewModel categoryViewModel : categoryViewModelList) {
                    System.out.println(categoryViewModel.getCategory().getName());
                    List<String> brand = categoryViewModel.getBrand();
                    for (String b : brand) {
                        System.out.println(b);
                    }
                }

                return categoryViewModelList;

            }
            else {
                categoryList.add(category);

                for(Category toReturnable : categoryList) {
                    List<CategoryMetaDataFieldValues> toReturnableFV = categoryMetadataFieldValuesRepository.findByCategoryId(toReturnable.getCategoryId());
                    List<String> allBrandName = productRepository.findAllBrandName(category.getCategoryId());

                    CategoryViewModel categoryViewModelObj = new CategoryViewModel();

                    categoryViewModelObj.setBrand(allBrandName);

                    categoryViewModelObj.setCategory(toReturnable);
                    categoryViewModelObj.setCategoryMetaDataFieldValuesList(toReturnableFV);

                    categoryViewModelList.add(categoryViewModelObj);
                }

                return categoryViewModelList;
            }
        }
        else {
            throw new ResourceNotFoundException("Category not found with the ID " + categoryId + " ");
        }
    }

}
