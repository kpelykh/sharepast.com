package kp.app.dal.dto.order;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@XmlAccessorType(XmlAccessType.FIELD) @XmlType(name = "productList") @XmlRootElement(name = "productList")
public class ProductListDTO {
  @XmlElement(name = "products", required = false)
  private List<ProductDTO> productDTOs = new ArrayList<ProductDTO>();
  
  public List<ProductDTO> getProductDTOs() {
    return productDTOs;
  }

  public void setProductDTOs(List<ProductDTO> productDTOs) {
    this.productDTOs = productDTOs;
  }    
  
  public void addAll(Set<ProductDTO> productDTOs) {
    this.productDTOs.addAll(productDTOs);
  }
}
