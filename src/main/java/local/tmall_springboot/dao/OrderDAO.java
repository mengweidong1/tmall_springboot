package local.tmall_springboot.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import local.tmall_springboot.pojo.Order;
import local.tmall_springboot.pojo.User;

public interface OrderDAO extends JpaRepository<Order, Integer> {
    public List<Order> findByUserAndStatusNotOrderByIdDesc(User user, String status);
}