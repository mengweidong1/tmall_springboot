package local.tmall_springboot.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import local.tmall_springboot.pojo.Category;
import local.tmall_springboot.pojo.OrderItem;
import local.tmall_springboot.pojo.User;
import local.tmall_springboot.service.CategoryService;
import local.tmall_springboot.service.OrderItemService;

public class OtherInterceptor implements HandlerInterceptor {
    @Autowired
    CategoryService categoryService;
    @Autowired
    OrderItemService orderItemService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o)
            throws Exception {
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
            ModelAndView modelAndView) throws Exception {
        HttpSession session = httpServletRequest.getSession();
        User user = (User) session.getAttribute("user");
        int cartTotalItemNumber = 0;
        if (user != null) {
            List<OrderItem> ois = orderItemService.listByUser(user);
            for (OrderItem oi : ois) {
                cartTotalItemNumber += oi.getNumber();
            }
        }

        List<Category> cs = categoryService.list();
        String contextPath = httpServletRequest.getServletContext().getContextPath();

        httpServletRequest.getServletContext().setAttribute("categories_below_search", cs);
        session.setAttribute("cartTotalItemNumber", cartTotalItemNumber);
        httpServletRequest.getServletContext().setAttribute("contextPath", contextPath);
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
            Object o, Exception e) throws Exception {
    }
}
