package local.tmall_springboot.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import local.tmall_springboot.pojo.Category;
import local.tmall_springboot.pojo.Property;

public interface PropertyDAO extends JpaRepository<Property, Integer> {
    // 根据分类进行查询
    Page<Property> findByCategory(Category category, Pageable pageable);
}