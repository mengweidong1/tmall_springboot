package local.tmall_springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import local.tmall_springboot.dao.CategoryDAO;
import local.tmall_springboot.pojo.Category;

// 标记这个类是 Service类
@Service
public class CategoryService {
    // 自动装配 上个步骤的 CategoryDAO 对象
    @Autowired
    CategoryDAO categoryDAO;

    // 首先创建一个 Sort 对象，表示通过 id 倒排序， 然后通过 categoryDAO进行查询。
    public List<Category> list() {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return categoryDAO.findAll(sort);
    }
}