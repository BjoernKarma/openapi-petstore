package de.openapi.petstore.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import de.openapi.petstore.db.OrderRepository;
import de.openapi.petstore.model.Order;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

  @Mock
  private OrderRepository orderRepository;
  private OrderService orderService;

  @BeforeEach
  public void setUp() {
    orderService = new OrderService(orderRepository, "mock");
  }

  @Test
  public void shouldReturnInventory() {
    var orders = List.of(
            new Order().quantity(100).status(Order.StatusEnum.APPROVED)
    );
    when(orderRepository.findAll()).thenReturn(orders);

    var inventory = orderService.getInventory();

    verify(orderRepository, times(2)).findAll();
    assertThat(inventory.size(), equalTo(1));
    assertThat(inventory, (IsMapContaining.hasEntry("approved", 100)));
  }

  @Test
  public void shouldReturnOrder() {
    when(orderRepository.findOrderById(anyLong())).thenReturn(new Order().id(1L));

    var order = orderService.getOrderById(1L);

    verify(orderRepository, times(1)).findOrderById(1L);
    assertThat(order.getId(), equalTo(1L));
  }

  @Test
  public void shouldPlaceOrder() {
    var newOrder = new Order().id(1L);
    when(orderRepository.save(any())).thenReturn(new Order().id(2L));

    var order = orderService.placeOrder(newOrder);
    verify(orderRepository, times(1)).save(newOrder);
    assertThat(order.getId(), equalTo(2L));
  }

  @Test
  public void shouldDeleteOrder() {
    orderService.deleteOrderById(1L);
    verify(orderRepository, times(1)).deleteById(1L);
  }

}
