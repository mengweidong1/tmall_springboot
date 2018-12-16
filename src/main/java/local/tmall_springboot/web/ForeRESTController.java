package local.tmall_springboot.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.HtmlUtils;

import local.tmall_springboot.comparator.ProductAllComparator;
import local.tmall_springboot.comparator.ProductDateComparator;
import local.tmall_springboot.comparator.ProductPriceComparator;
import local.tmall_springboot.comparator.ProductReviewComparator;
import local.tmall_springboot.comparator.ProductSaleCountComparator;
import local.tmall_springboot.pojo.Category;
import local.tmall_springboot.pojo.OrderItem;
import local.tmall_springboot.pojo.Product;
import local.tmall_springboot.pojo.ProductImage;
import local.tmall_springboot.pojo.PropertyValue;
import local.tmall_springboot.pojo.Review;
import local.tmall_springboot.pojo.User;
import local.tmall_springboot.service.CategoryService;
import local.tmall_springboot.service.OrderItemService;
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

        user.setPassword(password);
        userService.add(user);
        return Result.success();
    }

    @PostMapping("/forelogin")
    public Object login(@RequestBody User userParam, HttpSession session) {
        String name = userParam.getName();
        name = HtmlUtils.htmlEscape(name);

        User user = userService.get(name, userParam.getPassword());
        if (user == null) {
            String message = "账号密码错误";
            return Result.fail(message);
        } else {
            session.setAttribute("user", user);
            return Result.success();
        }
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

    @GetMapping("forecheckLogin")
    public Object checkLogin(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null)
            return Result.success();
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
}
