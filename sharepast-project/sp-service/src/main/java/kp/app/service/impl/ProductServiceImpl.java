package kp.app.service.impl;

import kp.app.dal.dao.ProductDAO;
import kp.app.dal.domain.order.Product;
import kp.app.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service public class ProductServiceImpl implements ProductService {
  @Autowired private ProductDAO productDAO;

  public Set<Product> getProducts() {
    return productDAO.getProducts();
  }
}
