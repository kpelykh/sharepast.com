package kp.app.dal.dto.order;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * Product Data transfer object.
 * 
 * @author Sanjay Acharya
 */
@XmlType(name = "product") 
@XmlRootElement(name = "product") 
public class ProductDTO  {

  private Long productId;

  private String name;

  private String description;

  public ProductDTO() {}

  public Long getProductId() {
    return productId;
  }

  public void setProductId(Long productId) {
    this.productId = productId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
