package com.sharepast.dal.exceptions;

/**
 * Order Exception.
 * 
 * @author Sanjay Acharya
 */
public class OrderException extends RuntimeException {
  private static final long serialVersionUID = -7466737637950908872L;

  public OrderException(String message) {
    super(message);
  }
  
  public OrderException(String message, Throwable t) {
    super(message, t);
  }
}
