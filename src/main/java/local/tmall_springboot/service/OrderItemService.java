package local.tmall_springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import local.tmall_springboot.dao.OrderItemDAO;
import local.tmall_springboot.pojo.Order;
import local.tmall_springboot.pojo.OrderItem;
import local.tmall_springboot.pojo.Product;
import local.tmall_springboot.pojo.User;

@Service
public class OrderItemService {
    @Autowired
    OrderItemDAO orderItemDAO;
    @Autowired
    ProductImageService productImageService;

    public void fill(List<Order> orders) {
        for (Order order : orders)
            fill(order);
    }

    public void update(OrderItem orderItem) {
        orderItemDAO.save(orderItem);
    }

    public void fill(Order order) {
        List<OrderItem> orderItems = listByOrder(order);
        float total = 0;
        int totalNumber = 0;
        for (OrderItem oi : orderItems) {
            total += oi.getNumber() * oi.getProduct().getPromotePrice();
            totalNumber += oi.getNumber();
            productImageService.setFirstProdutImage(oi.getProduct());
        }
        order.setTotal(total);
        order.setOrderItems(orderItems);
        order.setTotalNumber(totalNumber);
        order.setOrderItems(orderItems);
    }

    public void add(OrderItem orderItem) {
        orderItemDAO.save(orderItem);
    }

    public OrderItem get(int id) {
        return orderItemDAO.findOne(id);
    }

    public void delete(int id) {
        orderItemDAO.delete(id);
    }

    public int getSaleCount(Product product) {
        List<OrderItem> ois = listByProduct(product);
        int result = 0;
        for (OrderItem oi : ois) {
            if (null != oi.getOrder())
                if (null != oi.getOrder() && null != oi.getOrder().getPayDate())
                    result += oi.getNumber();
        }
        return result;
    }

    public List<OrderItem> listByProduct(Product product) {
        return orderItemDAO.findByProduct(product);
    }

    public List<OrderItem> listByOrder(Order order) {
        return orderItemDAO.findByOrderOrderByIdDesc(order);
    }

    public List<OrderItem> listByUser(User user) {
        return orderItemDAO.findByUserAndOrderIsNull(user);
    }
}