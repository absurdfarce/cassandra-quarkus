package com.datastax.oss.quarkus.dao;

import java.util.UUID;

import com.datastax.oss.driver.api.mapper.annotations.Dao;
import com.datastax.oss.driver.api.mapper.annotations.Select;
import com.datastax.oss.driver.api.mapper.annotations.Update;

@Dao
public interface ProductDao {

    @Update
    void update(Product product);

    @Select
    Product findById(UUID productId);

}