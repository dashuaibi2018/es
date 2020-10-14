package com.dna.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dna.entity.Product;
import com.dna.mapper.ProductMapper;
import org.springframework.stereotype.Service;

@Service
public class ProductService extends ServiceImpl<ProductMapper, Product> {
}
