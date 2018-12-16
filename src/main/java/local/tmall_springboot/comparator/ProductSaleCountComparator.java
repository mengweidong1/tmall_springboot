package local.tmall_springboot.comparator;

import java.util.Comparator;

import local.tmall_springboot.pojo.Product;

/**
 * 销量比较器 把 销量高的放前面
 * 
 * @author M431
 *
 */
public class ProductSaleCountComparator implements Comparator<Product> {

    @Override
    public int compare(Product p1, Product p2) {
        return p2.getSaleCount() - p1.getSaleCount();
    }

}
