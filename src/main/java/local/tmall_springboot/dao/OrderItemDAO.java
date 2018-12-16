package local.tmall_springboot.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import local.tmall_springboot.pojo.Order;
import local.tmall_springboot.pojo.OrderItem;
import local.tmall_springboot.pojo.Product;
import local.tmall_springboot.pojo.User;

public interface OrderItemDAO extends JpaRepository<OrderItem, Integer> {
    // 提供过了通过订单查询的方法
    List<OrderItem> findByOrderOrderByIdDesc(Order order);

    // 根据产品获取OrderItem的方法
    List<OrderItem> findByProduct(Product product);

    List<OrderItem> findByUserAndOrderIsNull(User user);

}