package local.tmall_springboot.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import local.tmall_springboot.pojo.Order;
import local.tmall_springboot.pojo.OrderItem;

public interface OrderItemDAO extends JpaRepository<OrderItem, Integer> {
    // 提供过了通过订单查询的方法
    List<OrderItem> findByOrderOrderByIdDesc(Order order);
}