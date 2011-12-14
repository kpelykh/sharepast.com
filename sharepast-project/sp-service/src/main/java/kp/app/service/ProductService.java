package kp.app.service;

import kp.app.dal.domain.order.Product;

import java.util.Set;

public interface ProductService {
  /**
   * @return A set of Products
   */
  public Set<Product> getProducts();
}
