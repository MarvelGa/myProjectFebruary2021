package com.hybris.ciklum.controller;

import com.hybris.ciklum.model.Order;
import com.hybris.ciklum.model.OrderItem;
import com.hybris.ciklum.model.Product;
import com.hybris.ciklum.repository.OrderItemRepository;
import com.hybris.ciklum.repository.OrderRepository;
import com.hybris.ciklum.repository.ProductRepository;
import com.hybris.ciklum.service.OrderItemService;
import com.hybris.ciklum.service.OrderService;
import com.hybris.ciklum.service.ProductService;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@Data
public class OrderController {
    private final Logger logger = LoggerFactory.getLogger(ProductController.class);
    @Autowired
    private final ProductService productService;
    @Autowired
    private final OrderService orderService;
    @Autowired
    private final OrderItemService orderItemService;
    @Autowired
    private final ProductRepository productRepository;
    @Autowired
    private final OrderItemRepository orderItemRepository;
    @Autowired
    private final OrderRepository orderRepository;


    @GetMapping("/create-order/add/{product_id}")
    public String createOrder(@PathVariable("product_id") long productId, Model model) {
        logger.info("Create order");
        Product product = productService.getProductById(productId);
        model.addAttribute("orderitem", new OrderItem());
        model.addAttribute("product", product);
        return "create-order";
    }

    @PostMapping("create-order/add/{product_id}")
    public String createOrder(@PathVariable("product_id") long productId, HttpSession session, @ModelAttribute Product product, OrderItem orderItem, BindingResult result) {
        logger.info("You are creating new order");
        if (result.hasErrors()) {
            logger.error(result.getAllErrors().toString());
            return "/create-order/add/" + productId;
        }
        Order order = new Order();
        if (session.getAttribute("order1") != null) {
            order = (Order) session.getAttribute("order1");
            List<Product> allProductInOrder = productService.getAllProductsByOrderId(order.getId());
            for (Product elem : allProductInOrder) {
                if (elem.getId().equals(productId)) {
                    OrderItem orderItem1 = (OrderItem) session.getAttribute("orderItem1");
                    int quantity = (Integer) session.getAttribute("quantity");
                    orderItem1.setQuantity(quantity + orderItem.getQuantity());
                    orderItemService.createOrUpdate(orderItem1);
                    session.invalidate();
                    return "redirect:/orders/" + order.getId();
                }
            }
        }

        Product product1 = productService.getProductById(productId);
        Order or = orderService.createOrUpdate(order);
        orderItemService.addProductToOrderItem(product1, orderItem);
        orderItemService.addOrderToOrderItem(or, orderItem);
        session.invalidate();
        return "redirect:/orders/" + or.getId();
    }


    @GetMapping("/orders/{order_id}")
    public String getAllOrders(@PathVariable("order_id") long orderId, @ModelAttribute Product product, BindingResult result, Model model) {
        logger.info("View of your order");
        List<OrderItem> orderItems = orderItemService.getOrderItemsByOrderId(orderId);
        int totalPrice = orderItemService.getTotalPrice(orderId);
        if (orderItems.size()!=0){
            OrderItem  orderItem = orderItems.get(0);
            model.addAttribute("orderItem", orderItem);
        }
        model.addAttribute("orderItems", orderItems);
        model.addAttribute("totalPrice", totalPrice);
        return "order-items";
    }


    @GetMapping("/order/delete/{order_id}")
    public String deleteOrderView(@PathVariable("order_id") long orderId) {
        logger.info("YOU DELETING THE ORDER, ARE YOU SURE?");
        orderItemService.deleteAllOderItemsAndOrderByOrderId(orderId);
        return "order-items";
    }

    @GetMapping("/orders")
    public String getAllOrdersByEntrance(Model model) {
        logger.info("View of all orders");
        List<OrderItem> orders = orderItemService.getAllOrderByEntrance();
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/orders/sort=asc")
    public String getAllOrdersByASC(Model model) {
        logger.info("View of sorting of product by ascending");
        List<OrderItem> orders = orderItemService.getAllOrderByASC();
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/orders/sort=desc")
    public String getAllOrdersByDESC(Model model) {
        logger.info("View of sorting of product by descending");
        List<OrderItem> orders = orderItemService.getAllOrderByDESC();
        model.addAttribute("orders", orders);
        return "orders";
    }

    @GetMapping("/orders/{order_id}/edit/{product_id}")
    public String updateOrder(@PathVariable("order_id") long orderId, @PathVariable("product_id") long productId, Model model) {
        logger.info("You are see page for editing existing order");
        Product product = productService.getProductById(productId);
        OrderItem orderItem = orderItemRepository.getOrderItemByOrderIdAndProductId(orderId, productId);
        model.addAttribute("product", product);
        model.addAttribute("orderItem", orderItem);
        return "update-order";
    }

    @PostMapping("/orders/{order_id}/edit/{product_id}")
    public String updateOrder(@PathVariable("order_id") long orderId, @PathVariable("product_id") long productId,
                              @RequestParam("quantity") int quantity, @Validated @ModelAttribute Product product,  BindingResult result) {
        logger.info("You are editing existing order");
        if (result.hasErrors()) {
            logger.error(result.getAllErrors().toString());
            return "update-order";
        }
        OrderItem findOrderItem = orderItemRepository.getOrderItemByOrderIdAndProductId(orderId, productId);
        findOrderItem.setQuantity(quantity);
        orderItemService.createOrUpdate(findOrderItem);
        return "redirect:/orders";
    }

    @GetMapping("/orders/{order_id}/edit/{product_id}/view")
    public String updateOrderView(@PathVariable("order_id") long orderId, @PathVariable("product_id") long productId, Model model) {
        logger.info("You are see an existing order");
        Product product = productService.getProductById(productId);
        OrderItem orderItem = orderItemRepository.getOrderItemByOrderIdAndProductId(orderId, productId);
        model.addAttribute("product", product);
        model.addAttribute("orderItem", orderItem);
        return "update-order";
    }

    @PostMapping("/orders/{order_id}/edit/{product_id}/view")
    public String updateOrderView(@PathVariable("order_id") long orderId, @PathVariable("product_id") long productId,
                                  @RequestParam("quantity") int quantity, @Validated @ModelAttribute Product product, BindingResult result) {
        logger.info("Editing existing order");
        if (result.hasErrors()) {
            logger.error(result.getAllErrors().toString());
            return "update-order";
        }
        OrderItem findOrderItem = orderItemRepository.getOrderItemByOrderIdAndProductId(orderId, productId);
        findOrderItem.setQuantity(quantity);
        orderItemService.createOrUpdate(findOrderItem);
        return "redirect:/orders/{order_id}";
    }

    @GetMapping("/orders/{orderItem_id}/delete/{order_id}/view")
    public String deleteAllOrder(@PathVariable("orderItem_id") long orderItemId, @PathVariable("order_id") long orderId) {
        logger.info("deleteAllOrder");
        orderItemService.deleteOrderItemAndOrder(orderItemService.getOrderItemById(orderItemId), orderService.getOrderById(orderId));
        return "redirect:/orders/{order_id}";
    }

    @GetMapping("/orders/{orderItem_id}/delete/{order_id}/view/{product_id}")
    public String deleteOneProductFromOrderView(@PathVariable("orderItem_id") long orderItemId) {
        logger.info("deleteOneProductFromOrderView");
        orderItemService.deleteOrderItemById(orderItemId);
        return "redirect:/orders/{order_id}";
    }

    @GetMapping("/orders/delete/{orderItem_id}")
    public String deleteOneProductFromOrder(@PathVariable("orderItem_id") long orderItemId) {
        logger.info("deleteOneProductFromOrder");
        orderItemService.deleteOrderItemById(orderItemId);
        return "redirect:/orders";
    }

    @GetMapping("/orders/{orderItem_id}/delete/{order_id}")
    public String deleteOrder(@PathVariable("orderItem_id") long orderItemId, @PathVariable("order_id") long orderId) {
        logger.info("deleteOrder");
        orderItemService.deleteOrderItemAndOrder(orderItemService.getOrderItemById(orderItemId), orderService.getOrderById(orderId));
        return "redirect:/orders";
    }

    @GetMapping("/order/{order_id}/add/{product_id}")
    public String addOneMoreProductToOrder(@PathVariable("order_id") long orderId, @PathVariable("product_id") long productId, @ModelAttribute Product product, HttpSession session, BindingResult result, Model model) {
        logger.info("addOneMoreProductToOrder");
        List<Product> products = productService.getAll();
        model.addAttribute("products", products);

        Order order1 = orderService.getOrderById(orderId);
        session.setAttribute("order1", order1);

        OrderItem orderItem1 = orderItemService.getOrderItemByOrderIdAndProductId(orderId, productId);
        session.setAttribute("orderItem1", orderItem1);
        session.setAttribute("quantity", orderItem1.getQuantity());
        return "products";
    }

}
