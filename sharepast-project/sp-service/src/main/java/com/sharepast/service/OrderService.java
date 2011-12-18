package com.sharepast.service;


import com.sharepast.dal.dao.OrderDAO;
import com.sharepast.dal.domain.order.Order;
import com.sharepast.dal.domain.order.Product;
import com.sharepast.dal.exceptions.OrderNotFoundException;

public interface OrderService {
  /**
   * Creates an Order
   * 
   * @param order Order
   * @return Order Identifier
   */
  public void persist(Order order);

  /**
   * Gets an Order provided the <code>orderId</code>
   * 
   * @param orderId Order Id
   * @return The Order
   */
  public Order getOrder(Long orderId) throws OrderNotFoundException;

  /**
   * Gets a Product provided the <code>id</code>
   * 
   * @param id A Product id
   * @return A Product
   */
  public Product getProduct(Long id);

  /**
   * @param orderId Id of the order to delete.
   */
  public void delete(Long orderId);

  /**
   * @param orderDao Order DAO
   */
  public void setOrderDao(OrderDAO orderDao);
}
