package local.tmall_springboot.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import local.tmall_springboot.pojo.Order;

public interface OrderDAO extends JpaRepository<Order, Integer> {
}