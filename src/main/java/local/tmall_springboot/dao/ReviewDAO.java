package local.tmall_springboot.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import local.tmall_springboot.pojo.Product;
import local.tmall_springboot.pojo.Review;

public interface ReviewDAO extends JpaRepository<Review, Integer> {

    List<Review> findByProductOrderByIdDesc(Product product);

    int countByProduct(Product product);

}