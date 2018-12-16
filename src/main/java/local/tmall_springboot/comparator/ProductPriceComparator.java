package local.tmall_springboot.comparator;

import java.util.Comparator;

import local.tmall_springboot.pojo.Product;

/**
 * 价格比较器 把 价格低的放前面
 * 
 * @author M431
 *
 */
public class ProductPriceComparator implements Comparator<Product> {

    @Override
    public int compare(Product p1, Product p2) {
        return (int) (p1.getPromotePrice() - p2.getPromotePrice());
    }

}
