package com.sharepast.dal.dao;


import com.sharepast.dal.domain.order.Order;
import com.sharepast.dal.exceptions.OrderNotFoundException;

/**
 * Order Data Access Object definition
 * 
 * @author Sanjay Acharya
 */
public interface OrderDAO {
  /**
   * Persists an Order.
   * 
   * @param order Order Object.
   */
  public void persist(Order order);

  /**
   * Gets an Order object.
   * 
   * @return an Order
   * @throws com.sharepast.dal.exceptions.OrderNotFoundException if the order cannot be found.
   */
  public Order getOrder(Long orderId) throws OrderNotFoundException;

  /**
   * Deletes the Order if exists.
   * 
   * @param orderId ID of Order to delete.
   * @return <code>true</code> if the order was actually deleted.
   */
  public boolean delete(Long orderId);
}
