package com.sharepast.dal.dto.order;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;

/**
 * An Order class that is the DTO or the to be marshalled object. Note: I have chosen to separate
 * out the data model from the DTO or marshalling object as I prefer to keep them separate.
 * 
 * @author Sanjay Acharya
 */
@XmlAccessorType(XmlAccessType.FIELD) @XmlType(name = "order", propOrder = { "orderId", "lineItems" }) @XmlRootElement(name = "order") public class OrderDTO
{  
  @XmlElement(name = "orderIdentifier", required = false) private Long orderId;

  @XmlElement(name = "lineItem", required = false) private ArrayList<LineItemDTO> lineItems;

  public OrderDTO() {
    lineItems = new ArrayList<LineItemDTO>();
  }

  public OrderDTO(Long orderId, ArrayList<LineItemDTO> lineItems) {
    this.orderId = orderId;
    this.lineItems = lineItems;
  }

  /**
   * @return the orderId
   */
  public Long getOrderId() {
    return orderId;
  }

  /**
   * @param orderId the orderId to set
   */
  public void setOrderId(Long orderId) {
    this.orderId = orderId;
  }

  /**
   * @return the lineItems
   */
  public ArrayList<LineItemDTO> getLineItems() {
    return lineItems;
  }

  /**
   * @param items the lineItems to set
   */
  public void setLineItems(ArrayList<LineItemDTO> items) {
    this.lineItems = items;
  }

  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
