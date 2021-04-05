package com.ShopOnline.Buy.online.repos;

import com.ShopOnline.Buy.online.entities.category.CategoryMetaDataField;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryMetadataFieldRepository extends CrudRepository<CategoryMetaDataField, Long> {

    Optional<CategoryMetaDataField> findByName(String name);

    List<CategoryMetaDataField> findAll(Pageable pageable);
}
