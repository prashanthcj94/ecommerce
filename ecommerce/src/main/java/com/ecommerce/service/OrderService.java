package com.ecommerce.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.ecommerce.entity.Order;
import com.ecommerce.entity.Product;
import com.ecommerce.entity.ShoppingCart;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;

@Component
@Service
public class OrderService {

    private OrderRepository orderRepository;
    private ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public Order getOrderDetail(int orderId) {
        Optional<Order> order = this.orderRepository.findById(orderId);
        return order.isPresent() ? order.get() : null;
    }

    public float getCartAmount(List<ShoppingCart> shoppingCartList) {

        float totalCartAmount = 0f;
        float singleCartAmount = 0f;
        int availableQuantity = 0;

        for (ShoppingCart cart : shoppingCartList) {

            int productId = cart.getProductId();
            Optional<Product> product = productRepository.findById(productId);
            if (product.isPresent()) {
                Product product1 = product.get();
                if (product1.getAvailableQuantity() < cart.getQuantity()) {
                    singleCartAmount = product1.getPrice() * product1.getAvailableQuantity();
                    cart.setQuantity(product1.getAvailableQuantity());
                } else {
                    singleCartAmount = cart.getQuantity() * product1.getPrice();
                    availableQuantity = product1.getAvailableQuantity() - cart.getQuantity();
                }
                totalCartAmount = totalCartAmount + singleCartAmount;
                product1.setAvailableQuantity(availableQuantity);
                availableQuantity=0;
                cart.setProductName(product1.getName());
                cart.setAmount(singleCartAmount);
                productRepository.save(product1);
            }
        }
        return totalCartAmount;
    }

    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }
}
