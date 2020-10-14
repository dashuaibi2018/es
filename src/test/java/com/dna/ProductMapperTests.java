package com.dna;

import com.dna.entity.Product;
import com.dna.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
/*
 * mysql-plus
 *
 * */
public class ProductMapperTests {

    @Autowired
    private ProductMapper productMapper;

    @Test
    public void query() {
        productMapper.selectList(null).forEach(System.out::println);
    }

    @Test
    public void insert() {
        Product product = new Product();
        product.setId(10);
        product.setName("Lombok");
        product.setPrice(21);
        product.setDesc("Sakura@baomidou.com");
        int insert = productMapper.insert(product);
        System.out.println(insert > 0 ? "插入成功" : "插入失败");
    }

    @Test
    public void update() {
        Product user = new Product();
        user.setId(6);
        user.setName("Lombok_update");
        int i = productMapper.updateById(user);
        System.out.println(i > 0 ? "修改成功" : "修改失败");
    }

    @Test
    public void delete() {
        Map<String, Object> condition = new HashMap<>();
        condition.put("name", "Lombok_update");
        int i = productMapper.deleteByMap(condition);
        System.out.println(i > 0 ? "删除成功" : "删除失败");
    }
}

