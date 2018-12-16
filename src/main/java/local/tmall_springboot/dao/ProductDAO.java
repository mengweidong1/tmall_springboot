package local.tmall_springboot.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import local.tmall_springboot.pojo.Category;
import local.tmall_springboot.pojo.Product;

public interface ProductDAO extends JpaRepository<Product, Integer> {
    Page<Product> findByCategory(Category category, Pageable pageable);

    // 通过分类查询所有产品的方法，因为这里不需要分页。
    List<Product> findByCategoryOrderById(Category category);

    // 根据名称进行模糊查询的方法
    List<Product> findByNameLike(String keyword, Pageable pageable);
}