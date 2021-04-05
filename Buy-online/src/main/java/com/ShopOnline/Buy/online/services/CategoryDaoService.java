package com.ShopOnline.Buy.online.services;

import com.ShopOnline.Buy.online.entities.category.Category;
import com.ShopOnline.Buy.online.entities.category.CategoryMetaDataFieldValues;
import com.ShopOnline.Buy.online.exceptions.BadRequestException;
import com.ShopOnline.Buy.online.exceptions.ResourceNotFoundException;
import com.ShopOnline.Buy.online.models.CategoryModel;
import com.ShopOnline.Buy.online.repos.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryDaoService {

    @Autowired
    CategoryRepository categoryRepository;

    public String addParentCategory(CategoryModel categoryModel) {
        Optional<Category> categoryOptional = categoryRepository.findByName(categoryModel.getName());
        if(categoryOptional.isPresent()) {
            throw new BadRequestException("Category already present in the database with name" + categoryModel.getName() + " ");
        }
        else {
            ModelMapper mapper = new ModelMapper();
            Category category = mapper.map(categoryModel, Category.class);

            categoryRepository.save(category);

            return "New category added in the database with name " + categoryModel.getName() + " ";
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

    public List<Category> getAllCategory(String page, String size) {
        Pageable pageable = PageRequest.of(Integer.parseInt(page),Integer.parseInt(size));

        return categoryRepository.findAll(pageable);
    }

    public Category getCategory(Long categoryId) {
        Optional<Category> categoryOptional = categoryRepository.findById(categoryId);
        if(categoryOptional.isPresent()) {
            return categoryOptional.get();
        }
        else {
            throw new ResourceNotFoundException("Category not found with the ID " + categoryId + " ");
        }
    }

    public List<Category> getAllRootLevelCategories() {
        return categoryRepository.findAllRootLevelCategory();
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
}
