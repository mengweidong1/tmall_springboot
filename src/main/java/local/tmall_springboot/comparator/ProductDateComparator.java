package local.tmall_springboot.comparator;

import java.util.Comparator;

import local.tmall_springboot.pojo.Product;

/**
 * 新品比较器 把 创建日期晚的放前面
 * 
 * @author M431
 *
 */
public class ProductDateComparator implements Comparator<Product> {

    @Override
    public int compare(Product p1, Product p2) {
        return p1.getCreateDate().compareTo(p2.getCreateDate());
    }
}
