package local.tmall_springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import local.tmall_springboot.dao.CategoryDAO;
import local.tmall_springboot.pojo.Category;
import local.tmall_springboot.util.Page4Navigator;

// 标记这个类是 Service类
@Service
public class CategoryService {
    // 自动装配 上个步骤的 CategoryDAO 对象
    @Autowired
    CategoryDAO categoryDAO;

    // 带参的 list 方法。
    public Page4Navigator<Category> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page pageFromJPA = categoryDAO.findAll(pageable);

        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

    // 首先创建一个 Sort 对象，表示通过 id 倒排序， 然后通过 categoryDAO进行查询。
    public List<Category> list() {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return categoryDAO.findAll(sort);
    }

    public void add(Category bean) {
        categoryDAO.save(bean);
    }

    public void delete(int id) {
        categoryDAO.delete(id);
    }

    public Category get(int id) {
        Category c = categoryDAO.findOne(id);
        return c;
    }

    public void update(Category bean) {
        categoryDAO.save(bean);
    }
}