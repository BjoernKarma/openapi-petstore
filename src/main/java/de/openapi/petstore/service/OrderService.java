package de.openapi.petstore.service;

import de.openapi.petstore.db.OrderRepository;
import de.openapi.petstore.model.Order;
import io.micrometer.observation.annotation.Observed;
import java.security.SecureRandom;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

// tag::javadoc[]

/**
 * A service that provides orders.
 */
// end::javadoc[]
@Component
@Observed(name = "de.openapi.petstore.OrderService")
public class OrderService {

  private final OrderRepository orderRepository;

  public OrderService(@Autowired OrderRepository orderRepository, @Value("${openapi.order.enabled}") String enabled) {
    this.orderRepository = orderRepository;
    if (enabled.equals("mock") && orderRepository.findAll().isEmpty()) {
      // initialization of db
      this.orderRepository.save(createOrder(1, 1, 100, new Date(), Order.StatusEnum.PLACED, true));
      this.orderRepository.save(createOrder(2, 1, 50, new Date(), Order.StatusEnum.APPROVED, true));
      this.orderRepository.save(
          createOrder(3, 1, 50, new Date(), Order.StatusEnum.DELIVERED, true));
    }
  }

  private static Order createOrder(final long id,
      final long petId,
      final int quantity,
      final Date shipDate,
      final Order.StatusEnum status,
      final boolean complete) {
    final Order order = new Order();
    order.setId(id);
    order.setPetId(petId);
    order.setComplete(complete);
    order.setQuantity(quantity);
    var offsetDateTime = shipDate.toInstant().atOffset(ZoneOffset.UTC);
    order.setShipDate(offsetDateTime);
    order.setStatus(status);
    return order;
  }

  public Map<String, Integer> getInventory() {
    final List<Order> orders = orderRepository.findAll();
    final Map<Order.StatusEnum, Integer> countByStatus = new EnumMap<>(Order.StatusEnum.class);

    for (final Order order : orders) {
      final Order.StatusEnum status = order.getStatus();
      if (countByStatus.containsKey(status)) {
        countByStatus.put(status, countByStatus.get(status) + order.getQuantity());
      } else {
        countByStatus.put(status, order.getQuantity());
      }
    }
    // Convert Enum to String for the keys
    return countByStatus.entrySet().stream().collect(
        Collectors.toMap(
            e -> e.getKey().getValue(),
            Entry::getValue));
  }

  public Order getOrderById(Long orderId) {
    return orderRepository.findOrderById(orderId);
  }

  public Order placeOrder(Order order) {
    order.setId(new SecureRandom().nextLong());
    return this.orderRepository.save(order);
  }

  public List<Order> findOrdersByStatus(final String status) {
    return orderRepository.findByStatusIn(
        Arrays.stream(status.split(","))
            .map(String::toUpperCase)
            .map(String::trim)
            .collect(Collectors.toList())
    );
  }

  public void deleteOrderById(Long orderId) {
    this.orderRepository.deleteById(orderId);
  }

}
