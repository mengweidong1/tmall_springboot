package local.tmall_springboot.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 后台管理页面跳转专用控制器。 因为是做前后端分离，所以数据是通过 RESTFUL接口来取的，而在业务上，除了 RESTFUL
 * 服务要提供，还要提供页面跳转服务，所以所有的后台页面跳转都放在 AdminPageController 这个控制器里。 而RSTFUL 专门放在
 * Category 对应的控制器 CategoryController.java 里面。 这样代码更清晰，不会搅起搅起的~
 * 
 */

// 表示这是一个控制器。
@Controller
public class AdminPageController {
    // 访问地址 admin,就会客户端跳转到 admin_category_list去。
    @GetMapping(value = "/admin")
    public String admin() {
        return "redirect:admin_category_list";
    }

    // 访问地址 admin_category_list 就会访问 admin/listCategory.html 文件。
    @GetMapping(value = "/admin_category_list")
    public String listCategory() {
        return "admin/listCategory";
    }
}