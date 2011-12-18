package com.sharepast.service.impl;

import com.sharepast.dal.dao.OrderDAO;
import com.sharepast.dal.dao.ProductDAO;
import com.sharepast.dal.exceptions.OrderNotFoundException;
import com.sharepast.service.OrderService;
import com.sharepast.dal.domain.order.LineItem;
import com.sharepast.dal.domain.order.Order;
import com.sharepast.dal.domain.order.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Default Implementation of Order Service. This is a mock implementation.
 * 
 * @author Sanjay Acharya
 */
@Service public class OrderServiceImpl implements OrderService {
  /**
   * Order DAO ref
   */
  @Autowired private OrderDAO orderDao;

  @Autowired private ProductDAO productDAO;

  public Product getProduct(Long id) {
    return productDAO.getProduct(id);
  }

  private void populateProduct(Order order) {
    for (LineItem lineItem : order.getLineItems()) {
      // Doing this as the product in line item might be a complete
      // product
      // object. This is for demo only not to be taken as any pattern :-)
      lineItem.setProduct(getProduct(lineItem.getProduct().getId()));
    }
  }

  public void persist(Order order) {
    populateProduct(order);
    System.out.println("Populated Order:" + order);
    orderDao.persist(order);
  }

  /**
   * Gets an Order provided the <code>orderId</code>
   * 
   * @param orderId Order Id
   * @return The Order
   * @throws OrderNotFoundException
   */
  public Order getOrder(Long orderId) throws OrderNotFoundException {
    Order order = orderDao.getOrder(orderId);
    return order;
  }

  /**
   * @param orderId Id of the order to delete.
   */
  public void delete(Long orderId) {
    orderDao.delete(orderId);
  }


  public void setOrderDao(OrderDAO orderDao) {
    this.orderDao = orderDao;
  }
}
