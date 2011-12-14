package kp.app.dal.dao;

import kp.app.dal.domain.order.Product;

import java.util.Set;

/**
 * Product Data Access Object definition.
 * 
 * @author Sanjay Acharya
 */
public interface ProductDAO {
  /**
   * @return Set of Products currently present.
   */
  public Set<Product> getProducts();

  /**
   * Gets a Specific product by <code>id</code>
   * 
   * @param id Product id
   * @return Product of a specific id.
   */
  public Product getProduct(Long id);
}
