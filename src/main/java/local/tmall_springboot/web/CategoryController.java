package local.tmall_springboot.web;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import local.tmall_springboot.pojo.Category;
import local.tmall_springboot.service.CategoryService;
import local.tmall_springboot.util.ImageUtil;
import local.tmall_springboot.util.Page4Navigator;

// 表示这是一个控制器，并且对每个方法的返回值都会直接转换为 json 数据格式。
@RestController
public class CategoryController {
    // 自动装配 CategoryService
    @Autowired
    CategoryService categoryService;

    // // 对于categories 访问，会获取所有的 Category对象集合，并返回这个集合。 因为是声明为 @RestController，
    // // 所以这个集合，又会被自动转换为 JSON数组抛给浏览器。
    // @GetMapping("/categories")
    // public List<Category> list() throws Exception {
    // return categoryService.list();
    // }

    // 修改原 list 方法，接受 start 和 size 参数。 返回的是 Page4Navigator 类型，并通过 RestController
    // 转换为 json 对象抛给浏览器。
    @GetMapping("/categories")
    public Page4Navigator<Category> list(@RequestParam(value = "start", defaultValue = "0") int start,
            @RequestParam(value = "size", defaultValue = "5") int size) throws Exception {
        start = start < 0 ? 0 : start;
        Page4Navigator<Category> page = categoryService.list(start, size, 5);
        return page;
    }

    @PostMapping("/categories")
    public Object add(Category bean, MultipartFile image, HttpServletRequest request) throws Exception {
        categoryService.add(bean);
        saveOrUpdateImageFile(bean, image, request);
        return bean;
    }

    public void saveOrUpdateImageFile(Category bean, MultipartFile image, HttpServletRequest request) throws Exception {
        // 接受上传图片，并保存到 img/category目录下
        File imageFolder = new File(request.getServletContext().getRealPath("img/category"));
        // 文件名使用新增分类的id
        File file = new File(imageFolder, bean.getId() + ".jpg");
        // 如果目录不存在，需要创建
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        // 进行文件复制
        image.transferTo(file);
        // 调用ImageUtil的change2jpg 进行文件类型强制转换为 jpg格式
        BufferedImage img = ImageUtil.change2jpg(file);
        // 保存图片
        ImageIO.write(img, "jpg", file);
    }

    @DeleteMapping("/categories/{id}")
    public String delete(@PathVariable("id") int id, HttpServletRequest request) throws Exception {
        categoryService.delete(id);
        File imageFolder = new File(request.getServletContext().getRealPath("img/category"));
        File file = new File(imageFolder, id + ".jpg");
        file.delete();
        return null;
    }

    @GetMapping("/categories/{id}")
    public Category get(@PathVariable("id") int id) throws Exception {
        Category bean = categoryService.get(id);
        return bean;
    }

    @PutMapping("/categories/{id}")
    public Object update(Category bean, MultipartFile image, HttpServletRequest request) throws Exception {
        String name = request.getParameter("name");
        bean.setName(name);
        categoryService.update(bean);

        if (image != null) {
            saveOrUpdateImageFile(bean, image, request);
        }
        return bean;
    }
}
