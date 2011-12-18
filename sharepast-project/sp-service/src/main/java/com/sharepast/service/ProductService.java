package com.sharepast.service;

import com.sharepast.dal.domain.order.Product;

import java.util.Set;

public interface ProductService {
  /**
   * @return A set of Products
   */
  public Set<Product> getProducts();
}
