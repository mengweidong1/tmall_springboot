package local.tmall_springboot.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import local.tmall_springboot.pojo.Product;
import local.tmall_springboot.pojo.Property;
import local.tmall_springboot.pojo.PropertyValue;

public interface PropertyValueDAO extends JpaRepository<PropertyValue, Integer> {
    // 根据产品查询
    List<PropertyValue> findByProductOrderByIdDesc(Product product);

    // 根据产品和属性获取PropertyValue对象
    PropertyValue getByPropertyAndProduct(Property property, Product product);

}