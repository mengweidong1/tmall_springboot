package local.tmall_springboot.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import local.tmall_springboot.pojo.User;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o)
            throws Exception {
        HttpSession session = httpServletRequest.getSession();
        // 返回根路径
        String contextPath = session.getServletContext().getContextPath();
        // 准备字符串数组 requireAuthPages，存放那些需要登录才能访问的路径
        String[] requireAuthPages = new String[] { "buy", "alipay", "payed", "cart", "bought", "confirmPay",
                "orderConfirmed",

                "forebuyone", "forebuy", "foreaddCart", "forecart", "forechangeOrderItem", "foredeleteOrderItem",
                "forecreateOrder", "forepayed", "forebought", "foreconfirmPay", "foreorderConfirmed", "foredeleteOrder",
                "forereview", "foredoreview"

        };
        // 获取uri
        String uri = httpServletRequest.getRequestURI();
        // 去掉前缀/tmall_springboot
        uri = StringUtils.remove(uri, contextPath + "/");
        String page = uri;
        // 判断是否是以 requireAuthPages 里的开头的
        if (begingWith(page, requireAuthPages)) {
            // 如果是就判断是否登陆，未登陆就跳转到 login 页面
            User user = (User) session.getAttribute("user");
            if (user == null) {
                httpServletResponse.sendRedirect("login");
                return false;
            }
        }
        // 如果不是就放行
        return true;
    }

    private boolean begingWith(String page, String[] requiredAuthPages) {
        boolean result = false;
        for (String requiredAuthPage : requiredAuthPages) {
            if (StringUtils.startsWith(page, requiredAuthPage)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o,
            ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
            Object o, Exception e) throws Exception {
    }
}