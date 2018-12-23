package local.tmall_springboot.web;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import local.tmall_springboot.comparator.ProductAllComparator;
import local.tmall_springboot.comparator.ProductDateComparator;
import local.tmall_springboot.comparator.ProductPriceComparator;
import local.tmall_springboot.comparator.ProductReviewComparator;
import local.tmall_springboot.comparator.ProductSaleCountComparator;
import local.tmall_springboot.pojo.Category;
import local.tmall_springboot.pojo.Order;
import local.tmall_springboot.pojo.OrderItem;
import local.tmall_springboot.pojo.Product;
import local.tmall_springboot.pojo.ProductImage;
import local.tmall_springboot.pojo.PropertyValue;
import local.tmall_springboot.pojo.Review;
import local.tmall_springboot.pojo.User;
import local.tmall_springboot.service.CategoryService;
import local.tmall_springboot.service.OrderItemService;
import local.tmall_springboot.service.OrderService;
import local.tmall_springboot.service.ProductImageService;
import local.tmall_springboot.service.ProductService;
import local.tmall_springboot.service.PropertyValueService;
import local.tmall_springboot.service.ReviewService;
import local.tmall_springboot.service.UserService;
import local.tmall_springboot.util.Result;

/**
 * 专门用来对应前台页面的路径
 * 
 * @author Administrator
 *
 */

@RestController
public class ForeRESTController {
    @Autowired
    CategoryService categoryService;
    @Autowired
    ProductService productService;
    @Autowired
    UserService userService;
    @Autowired
    ProductImageService productImageService;
    @Autowired
    PropertyValueService propertyValueService;
    @Autowired
    ReviewService reviewService;
    @Autowired
    OrderItemService orderItemService;
    @Autowired
    OrderService orderService;

    @GetMapping("/forehome")
    public Object home() {
        // 查询所有分类
        List<Category> categories = categoryService.list();
        // 为这些分类填充产品集合
        productService.fill(categories);
        // 为这些分类填充推荐产品集合
        productService.fillByRow(categories);
        // 移除产品里的分类信息，以免出现重复递归
        categoryService.removeCategoryFromProduct(categories);
        return categories;
    }

    @PostMapping("/foreregister")
    public Object register(@RequestBody User user) {
        String name = user.getName();
        String password = user.getPassword();
        name = HtmlUtils.htmlEscape(name);
        user.setName(name);
        boolean exist = userService.isExist(name);

        if (exist) {
            String message = "用户名已经被使用，不能使用";
            return Result.fail(message);
        }

        // 注册时候的时候，会通过随机方式创建盐， 并且加密算法采用 "md5", 除此之外还会进行 2次加密。
        String salt = new SecureRandomNumberGenerator().nextBytes().toString();
        int times = 2;
        String algorithmName = "md5";

        String encodedPassword = new SimpleHash(algorithmName, password, salt, times).toString();

        user.setSalt(salt);
        user.setPassword(encodedPassword);
        userService.add(user);
        return Result.success();
    }

    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session) {
        String name = userParam.getName();
        name = HtmlUtils.htmlEscape(name);

        // 登陆的时候， 通过 Shiro的方式进行校验
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(name, userParam.getPassword());
        try {
            subject.login(token);
            User user = userService.getByName(name);
            session.setAttribute("user", user);
            return Result.success();
        } catch (AuthenticationException e) {
            String message = "账号密码错误";
            return Result.fail(message);
        }

        // User user = userService.get(name, userParam.getPassword());
        // if (user == null) {
        // String message = "账号密码错误";
        // return Result.fail(message);
        // } else {
        // session.setAttribute("user", user);
        // return Result.success();
        // }
    }

    // 通过 subject.logout 退出。
    @GetMapping("/forelogout")
    public String logout() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated())
            subject.logout();
        return "redirect:home";
    }

    @GetMapping("/foreproduct/{pid}")
    public Object product(@PathVariable("pid") int pid) {
        Product product = productService.get(pid);

        List<ProductImage> productSingleImages = productImageService.listSingleProductImages(product);
        List<ProductImage> productDetailImages = productImageService.listDetailProductImages(product);
        product.setProductSingleImages(productSingleImages);
        product.setProductDetailImages(productDetailImages);

        List<PropertyValue> pvs = propertyValueService.list(product);
        List<Review> reviews = reviewService.list(product);
        productService.setSaleAndReviewNumber(product);
        productImageService.setFirstProdutImage(product);

        Map<String, Object> map = new HashMap<>();
        map.put("product", product);
        map.put("pvs", pvs);
        map.put("reviews", reviews);

        return Result.success(map);
    }

    // 判断是否登录
    @GetMapping("forecheckLogin")
    public Object checkLogin(HttpSession session) {
        // 改为Shiro 方式
        Subject subject = SecurityUtils.getSubject();
        // User user = (User) session.getAttribute("user");
        // if (user != null)
        if (subject.isAuthenticated())
            return Result.success();
        else
            return Result.fail("未登录");
    }

    @GetMapping("forecategory/{cid}")
    public Object category(@PathVariable int cid, String sort) {
        Category category = categoryService.get(cid);
        productService.fill(category);
        productService.setSaleAndReviewNumber(category.getProducts());
        categoryService.removeCategoryFromProduct(category);
        if (sort != null) {
            switch (sort) {
            case "review":
                Collections.sort(category.getProducts(), new ProductReviewComparator());
                break;
            case "date":
                Collections.sort(category.getProducts(), new ProductDateComparator());
                break;
            case "saleCount":
                Collections.sort(category.getProducts(), new ProductSaleCountComparator());
                break;
            case "price":
                Collections.sort(category.getProducts(), new ProductPriceComparator());
                break;
            case "all":
                Collections.sort(category.getProducts(), new ProductAllComparator());
                break;
            }
        }
        return category;
    }

    @PostMapping("foresearch")
    public Object search(String keyword) {
        if (null == keyword)
            keyword = "";
        List<Product> ps = productService.search(keyword, 0, 20);
        productImageService.setFirstProdutImages(ps);
        productService.setSaleAndReviewNumber(ps);
        return ps;
    }

    @GetMapping("forebuyone")
    public Object buyone(int pid, int num, HttpSession session) {
        return buyoneAndAddCart(pid, num, session);
    }

    private int buyoneAndAddCart(int pid, int num, HttpSession session) {
        // 根据pid获取产品对象p
        Product product = productService.get(pid);
        int oiid = 0;
        // 从session中获取用户对象user
        User user = (User) session.getAttribute("user");
        boolean found = false;
        // 如果已经存在这个产品对应的OrderItem，并且还没有生成订单，即还在购物车中。 那么就应该在对应的OrderItem基础上，调整数量
        // 1 基于用户对象user，查询没有生成订单的订单项集合
        List<OrderItem> ois = orderItemService.listByUser(user);
        // 2 遍历这个集合
        for (OrderItem oi : ois) {
            // 3 如果产品是一样的话，就进行数量追加
            if (oi.getProduct().getId() == product.getId()) {
                oi.setNumber(oi.getNumber() + num);
                orderItemService.update(oi);
                found = true;
                // 4 获取这个订单项的 id
                oiid = oi.getId();
                break;
            }
        }

        // 如果不存在对应的OrderItem,那么就新增一个订单项OrderItem
        if (!found) {
            // 1 生成新的订单项
            OrderItem oi = new OrderItem();
            // 2 设置数量，用户和产品
            oi.setUser(user);
            oi.setProduct(product);
            oi.setNumber(num);
            // 3 插入到数据库
            orderItemService.add(oi);
            // 4 获取这个订单项的 id
            oiid = oi.getId();
        }
        // 返回当前订单项id
        return oiid;
        // 在页面上，拿到这个订单项id，就跳转到 location.href="buy?oiid="+oiid;
    }

    @GetMapping("forebuy")
    public Object buy(String[] oiid, HttpSession session) {
        // 准备一个泛型是OrderItem的集合ois
        List<OrderItem> orderItems = new ArrayList<>();
        float total = 0;

        for (String strid : oiid) {
            // 根据前面步骤获取的oiids，从数据库中取出OrderItem对象，并放入ois集合中
            int id = Integer.parseInt(strid);
            OrderItem oi = orderItemService.get(id);
            // 累计这些ois的价格总数，赋值在total上
            total += oi.getProduct().getPromotePrice() * oi.getNumber();
            orderItems.add(oi);
        }

        productImageService.setFirstProdutImagesOnOrderItems(orderItems);
        // 把订单项集合放在session的属性 "ois" 上
        session.setAttribute("ois", orderItems);

        // 把订单集合和total 放在map里
        Map<String, Object> map = new HashMap<>();
        map.put("orderItems", orderItems);
        map.put("total", total);
        return Result.success(map);
    }

    @GetMapping("foreaddCart")
    public Object addCart(int pid, int num, HttpSession session) {
        buyoneAndAddCart(pid, num, session);
        return Result.success();
    }

    @GetMapping("forecart")
    public Object cart(HttpSession session) {
        // 通过session获取当前用户
        // 一定要登录才访问，否则拿不到用户对象,会报错
        User user = (User) session.getAttribute("user");
        List<OrderItem> ois = orderItemService.listByUser(user);
        productImageService.setFirstProdutImagesOnOrderItems(ois);
        return ois;
    }

    @GetMapping("forechangeOrderItem")
    public Object changeOrderItem(HttpSession session, int pid, int num) {
        // 判断用户是否登录
        User user = (User) session.getAttribute("user");
        if (null == user)
            return Result.fail("未登录");

        // 遍历出用户当前所有的未生成订单的OrderItem
        List<OrderItem> ois = orderItemService.listByUser(user);
        for (OrderItem oi : ois) {
            // 根据pid找到匹配的OrderItem，并修改数量后更新到数据库
            if (oi.getProduct().getId() == pid) {
                oi.setNumber(num);
                orderItemService.update(oi);
                break;
            }
        }
        return Result.success();
    }

    @GetMapping("foredeleteOrderItem")
    public Object deleteOrderItem(HttpSession session, int oiid) {
        User user = (User) session.getAttribute("user");
        if (null == user)
            return Result.fail("未登录");
        orderItemService.delete(oiid);
        return Result.success();
    }

    @PostMapping("forecreateOrder")
    public Object createOrder(@RequestBody Order order, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (null == user)
            return Result.fail("未登录");
        // 根据当前时间加上一个4位随机数生成订单号
        String orderCode = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) + RandomUtils.nextInt(10000);
        // 根据上述参数，创建订单对象
        order.setOrderCode(orderCode);
        order.setCreateDate(new Date());
        order.setUser(user);
        // 把订单状态设置为等待支付
        order.setStatus(OrderService.waitPay);
        // 从session中获取订单项集合 ( 在结算功能的ForeRESTController.buy() ，订单项集合被放到了session中
        // )
        List<OrderItem> ois = (List<OrderItem>) session.getAttribute("ois");
        // 把订单加入到数据库，并且遍历订单项集合，设置每个订单项的order，更新到数据库
        float total = orderService.add(order, ois);
        // 统计本次订单的总金额
        Map<String, Object> map = new HashMap<>();
        map.put("oid", order.getId());
        map.put("total", total);

        return Result.success(map);
    }

    @GetMapping("forepayed")
    public Object payed(int oid) {
        Order order = orderService.get(oid);
        // 修改订单对象的状态和支付时间
        order.setStatus(OrderService.waitDelivery);
        order.setPayDate(new Date());
        // 更新这个订单对象到数据库
        orderService.update(order);
        return order;
    }

    @GetMapping("forebought")
    public Object bought(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (null == user)
            return Result.fail("未登录");
        List<Order> os = orderService.listByUserWithoutDelete(user);
        orderService.removeOrderFromOrderItem(os);
        return os;
    }

    @GetMapping("foreconfirmPay")
    public Object confirmPay(int oid) {
        Order o = orderService.get(oid);
        // 为订单对象填充订单项
        orderItemService.fill(o);
        orderService.cacl(o);
        // 把订单项上的订单对象移除，否则会导致重复递归
        orderService.removeOrderFromOrderItem(o);
        return o;
    }

    @PutMapping("foredeleteOrder")
    public Object deleteOrder(int oid) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.delete);
        orderService.update(o);
        return Result.success();
    }

    @GetMapping("forereview")
    public Object review(int oid) {
        Order o = orderService.get(oid);
        orderItemService.fill(o);
        orderService.removeOrderFromOrderItem(o);
        // 获取第一个订单项对应的产品,因为在评价页面需要显示一个产品图片，那么就使用这第一个产品的图片了。（这里没有对订单里的每种产品都评价，因为复杂度就比较高了，初学者学起来太吃力，有可能就放弃学习了，所以考虑到学习的平滑性，就仅仅提供对第一个产品的评价）
        Product p = o.getOrderItems().get(0).getProduct();
        // 获取这个产品的评价集合
        List<Review> reviews = reviewService.list(p);
        // 为产品设置评价数量和销量
        productService.setSaleAndReviewNumber(p);
        Map<String, Object> map = new HashMap<>();
        map.put("p", p);
        map.put("o", o);
        map.put("reviews", reviews);

        return Result.success(map);
    }

    @PostMapping("foredoreview")
    public Object doreview(HttpSession session, int oid, int pid, String content) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.finish);
        orderService.update(o);

        Product p = productService.get(pid);
        content = HtmlUtils.htmlEscape(content);

        User user = (User) session.getAttribute("user");
        Review review = new Review();
        review.setContent(content);
        review.setProduct(p);
        review.setCreateDate(new Date());
        review.setUser(user);
        reviewService.add(review);
        return Result.success();
    }

    @GetMapping("foreorderConfirmed")
    public Object orderConfirmed(int oid) {
        Order o = orderService.get(oid);
        o.setStatus(OrderService.waitReview);
        o.setConfirmDate(new Date());
        orderService.update(o);
        return Result.success();
    }
}
