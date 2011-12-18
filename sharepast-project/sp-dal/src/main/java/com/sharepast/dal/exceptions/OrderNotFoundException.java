package com.sharepast.dal.exceptions;

/**
 * Exception thrown when an order could not be retrieved.
 * 
 * @author Sanjay Acharya
 */
public class OrderNotFoundException extends OrderException {
  private static final long serialVersionUID = 7435087356659934123L;

  public OrderNotFoundException(String message) {
    super(message);
  }
  
  public OrderNotFoundException(String message, Throwable t) {
    super(message, t);
  }
}
