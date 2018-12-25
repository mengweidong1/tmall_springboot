package local.tmall_springboot.es;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import local.tmall_springboot.pojo.Product;

public interface ProductESDAO extends ElasticsearchRepository<Product, Integer> {

}
