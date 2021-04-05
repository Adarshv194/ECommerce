package com.ShopOnline.Buy.online.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.persistence.AttributeConverter;
import javax.print.attribute.Attribute;
import java.util.Map;

public class HashMapCoverter implements AttributeConverter<Map<String,Object>,String> {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        String toSaveInDatabase = null;
        try {
            toSaveInDatabase = objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return toSaveInDatabase;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        Map<String,Object> map = null;

        try {
            map = objectMapper.readValue(dbData,Map.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return map;
    }
}
