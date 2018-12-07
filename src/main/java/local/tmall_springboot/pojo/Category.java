package local.tmall_springboot.pojo;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// 表示这是一个实体类
@Entity
// 表示对应的表名是 category
@Table(name = "category")
/*
 * 因为是做前后端分离，而前后端数据交互用的是 json 格式。 那么 Category 对象就会被转换为 json 数据。 而本项目使用 jpa
 * 来做实体类的持久化，jpa 默认会使用 hibernate, 在 jpa 工作过程中，就会创造代理类来继承 Category ，并添加 handler 和
 * hibernateLazyInitializer 这两个无须 json 化的属性，所以这里需要用 JsonIgnoreProperties
 * 把这两个属性忽略掉。
 */
@JsonIgnoreProperties({ "handler", "hibernateLazyInitializer" })

public class Category {
    // @Id 标注用于声明一个实体类的属性映射为数据库的主键列
    @Id
    // 用于标注主键的生成策略，通过strategy 属性指定; –IDENTITY：采用数据库ID自增长的方式来自增主键字段，
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // 用来标识实体类中属性与数据表中字段的对应关系
    @Column(name = "id")
    int id;

    String name;

    // 瞬时字段products和productsByRow
    @Transient
    List<Product> products; // 一个分类下有多个产品
    @Transient
    List<List<Product>> productsByRow; // 一个分类又对应多个
                                       // List<Product>，提供这个属性，是为了在首页竖状导航的分类名称右边显示推荐产品列表

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<List<Product>> getProductsByRow() {
        return productsByRow;
    }

    public void setProductsByRow(List<List<Product>> productsByRow) {
        this.productsByRow = productsByRow;
    }

    @Override
    public String toString() {
        return "Category [id=" + id + ", name=" + name + "]";
    }
}