package local.tmall_springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import local.tmall_springboot.dao.PropertyValueDAO;
import local.tmall_springboot.pojo.Product;
import local.tmall_springboot.pojo.Property;
import local.tmall_springboot.pojo.PropertyValue;

@Service
public class PropertyValueService {

    @Autowired
    PropertyValueDAO propertyValueDAO;
    @Autowired
    PropertyService propertyService;

    public void update(PropertyValue bean) {
        propertyValueDAO.save(bean);
    }

    /**
     * 初始化PropertyValue 对于PropertyValue的管理，没有增加，只有修改。 所以需要通过初始化来进行自动地增加，以便于后面的修改
     * 
     * @param product
     */
    public void init(Product product) {
        // 根据产品获取分类，然后获取这个分类下的所有属性集合
        List<Property> propertys = propertyService.listByCategory(product.getCategory());
        for (Property property : propertys) {
            // 用属性id和产品id去查询，看看这个属性和这个产品，是否已经存在属性值了
            PropertyValue propertyValue = getByPropertyAndProduct(product, property);
            // 如果不存在，那么就创建一个属性值，并设置其属性和产品，接着插入到数据库中
            if (null == propertyValue) {
                propertyValue = new PropertyValue();
                propertyValue.setProduct(product);
                propertyValue.setProperty(property);
                propertyValueDAO.save(propertyValue);
            }
        }
    }

    public PropertyValue getByPropertyAndProduct(Product product, Property property) {
        return propertyValueDAO.getByPropertyAndProduct(property, product);
    }

    public List<PropertyValue> list(Product product) {
        return propertyValueDAO.findByProductOrderByIdDesc(product);
    }

}