package com.sharepast.service.impl;

import com.sharepast.dal.dao.ProductDAO;
import com.sharepast.dal.domain.order.Product;
import com.sharepast.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service public class ProductServiceImpl implements ProductService {
  @Autowired private ProductDAO productDAO;

  public Set<Product> getProducts() {
    return productDAO.getProducts();
  }
}
