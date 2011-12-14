package kp.app.dal.dto.order;


import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Defines a Line Item of an Order
 * 
 * @author sacharya
 */
@XmlType(name = "lineItem", propOrder = { "lineItemId", "itemId", "itemName", "quantity" }) 
@XmlRootElement(name = "lineItem") 
public class LineItemDTO {

  private Long lineItemId;

  private Long itemId;

  private String itemName;

  private int quantity;

  public LineItemDTO() {}

  /**
   * @return the lineItemId
   */
  public Long getLineItemId() {
    return lineItemId;
  }

  /**
   * @param lineItemId the lineItemId to set
   */
  public void setLineItemId(Long lineItemId) {
    this.lineItemId = lineItemId;
  }

  /**
   * @return the itemName
   */
  public String getItemName() {
    return itemName;
  }

  /**
   * @param itemName the itemName to set
   */
  public void setItemName(String itemName) {
    this.itemName = itemName;
  }

  /**
   * @return the quantity
   */
  public int getQuantity() {
    return quantity;
  }

  /**
   * @param quantity the quantity to set
   */
  public void setQuantity(int quantity) {
    this.quantity = quantity;
  }

  public Long getItemId() {
    return itemId;
  }

  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }
}
