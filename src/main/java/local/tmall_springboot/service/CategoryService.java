package local.tmall_springboot.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import local.tmall_springboot.dao.CategoryDAO;
import local.tmall_springboot.pojo.Category;
import local.tmall_springboot.pojo.Product;
import local.tmall_springboot.util.Page4Navigator;

// 标记这个类是 Service类
@Service
// 表示分类在缓存里的keys，都是归 "categories" 这个管理的
@CacheConfig(cacheNames = "categories")
public class CategoryService {
    // 自动装配 上个步骤的 CategoryDAO 对象
    @Autowired
    CategoryDAO categoryDAO;

    // 分页查询
    // 数据是一个集合。 （保存在 redis 里是一个 json 数组）， 命名为categories-page-xx-xx
    @Cacheable(key = "'categories-page-'+#p0+ '-' + #p1")
    // 带参的 list 方法。
    public Page4Navigator<Category> list(int start, int size, int navigatePages) {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Pageable pageable = new PageRequest(start, size, sort);
        Page pageFromJPA = categoryDAO.findAll(pageable);

        return new Page4Navigator<>(pageFromJPA, navigatePages);
    }

    @Cacheable(key = "'categories-all'")
    // 首先创建一个 Sort 对象，表示通过 id 倒排序， 然后通过 categoryDAO进行查询。
    public List<Category> list() {
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        return categoryDAO.findAll(sort);
    }

    // 增加，删除和修改用的都是这个注解, 其意义是删除 categories~keys 里的所有的keys.
    // 如果用@CachePut(key="'category-one-'+ #p0")，可以成功的操作，但是它并不能更新分页缓存
    // categories-page-0-5 里的数据，为了做到 同时更新缓存分页缓存里的数据，会超级的复杂，而且超级容易出错，其开发量也会非常大。
    // 所以最后，采用折中的办法，即，一旦增加了某个分类数据，那么就把缓存里所有分类相关的数据，都清除掉。
    @CacheEvict(allEntries = true)
    public void add(Category bean) {
        categoryDAO.save(bean);
    }

    @CacheEvict(allEntries = true)
    public void delete(int id) {
        categoryDAO.delete(id);
    }

    // 获取一个
    // 第一次访问的时候， redis 是不会有数据的，所以就会通过 jpa 到数据库里去取出来，一旦取出来之后，就会放在
    // redis里，命名为categories-one-xx
    // 第二次访问的时候，redis 就有数据了，就不会从数据库里获取了。
    @Cacheable(key = "'categories-one-'+ #p0")
    public Category get(int id) {
        Category c = categoryDAO.findOne(id);
        return c;
    }

    @CacheEvict(allEntries = true)
    public void update(Category bean) {
        categoryDAO.save(bean);
    }

    /**
     * 删除Product对象上的 分类
     * 
     * @param cs
     */
    public void removeCategoryFromProduct(List<Category> cs) {
        for (Category category : cs) {
            removeCategoryFromProduct(category);
        }
    }

    public void removeCategoryFromProduct(Category category) {
        List<Product> products = category.getProducts();
        if (null != products) {
            for (Product product : products) {
                product.setCategory(null);
            }
        }
        List<List<Product>> productsByRow = category.getProductsByRow();
        if (null != productsByRow) {
            for (List<Product> ps : productsByRow) {
                for (Product p : ps) {
                    p.setCategory(null);
                }
            }
        }
    }
}