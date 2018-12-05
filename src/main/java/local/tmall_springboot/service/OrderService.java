package local.tmall_springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import local.tmall_springboot.dao.OrderDAO;
import local.tmall_springboot.pojo.Order;
import local.tmall_springboot.pojo.OrderItem;
import local.tmall_springboot.util.Page4Navigator;

@Service
public class OrderService {
    public static final String waitPay = "waitPay";
    public static final String waitDelivery = "waitDelivery";
    public static final String waitConfirm = "waitConfirm";
    public static final String waitReview = "waitReview";
    public static final String finish = "finish";
    public static final String delete = "delete";

    @Autowired
    OrderDAO orderDAO;

    public Page4Navigator<Order> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page pageFromJPA = orderDAO.findAll(pageable);
        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

    public void removeOrderFromOrderItem(List<Order> orders) {
        for (Order order : orders) {
            removeOrderFromOrderItem(order);
        }
    }

    // 把订单里的订单项的订单属性设置为空
    /*
     * 为什么要做这个事情呢？ 因为SpingMVC ( springboot 里内置的mvc框架是 这个东西)的 RESTFUL
     * 注解，在把一个Order转换为json的同时，会把其对应的 orderItems 转换为 json数组， 而 orderItem对象上有
     * order属性， 这个order 属性又会被转换为 json对象，然后这个 order 下又有 orderItems 。。。
     * 就这样就会产生无穷递归，系统就会报错了。 如果标记成了 @JsonIgnoreProperties 会在和 Redis 整合的时候有 Bug,
     * 所以还是采用这种方式比较好。
     */
    private void removeOrderFromOrderItem(Order order) {
        List<OrderItem> orderItems = order.getOrderItems();
        for (OrderItem orderItem : orderItems) {
            orderItem.setOrder(null);
        }
    }

    public Order get(int oid) {
        return orderDAO.findOne(oid);
    }

    public void update(Order bean) {
        orderDAO.save(bean);
    }

}