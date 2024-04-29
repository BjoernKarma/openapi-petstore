package de.openapi.petstore.controller;

import de.openapi.petstore.api.StoreApi;
import de.openapi.petstore.model.Order;
import de.openapi.petstore.model.Pet;
import de.openapi.petstore.model.Session;
import de.openapi.petstore.service.OrderService;
import de.openapi.petstore.service.PetService;
import de.openapi.petstore.service.SessionService;
import io.micrometer.observation.annotation.Observed;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/petstore/v1")
@Observed(name = "de.openapi.petstore.StoreApi")
public class StoreApiController implements StoreApi {

  private static final Logger LOGGER = LoggerFactory.getLogger(StoreApiController.class);

  private final OrderService orderService;
  private final SessionService sessionService;
  private final PetService petService;

  @Autowired
  public StoreApiController(OrderService orderService, SessionService sessionService,
      PetService petService) {
    this.orderService = orderService;
    this.sessionService = sessionService;
    this.petService = petService;
  }

  public ResponseEntity<Void> deleteOrder(
      @Parameter(name = "orderId", description = "ID of the order that needs to be deleted", required = true, schema = @Schema(description = "")) @PathVariable("orderId") Long orderId) {

    LOGGER.info(
        "Hello from demo-petstore-service!\nThis log message was produced at {}#deleteOrder",
        getClass().getCanonicalName());

    if (orderId == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    orderService.deleteOrderById(orderId);

    final Order order = orderService.getOrderById(orderId);

    if (null == order) {
      return ResponseEntity.ok().body(null);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_MODIFIED).body(null);
    }
  }

  public ResponseEntity<List<Order>> findOrdersByStatus(
      @Parameter(name = "status", description = "Status values that need to be considered for filter", schema = @Schema(description = "", allowableValues = {
          "placed", "approved",
          "delivered"}, defaultValue = "placed")) @Valid @RequestParam(value = "status", required = false, defaultValue = "placed") String status) {

    LOGGER.info(
        "Hello from demo-petstore-service!\nThis log message was produced at {}#findOrdersByStatus",
        getClass().getCanonicalName());

    if (status == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    var orders = orderService.findOrdersByStatus(status);
    if (orders != null) {
      return ResponseEntity.ok().body(orders);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
  }

  public ResponseEntity<Map<String, Integer>> getInventory() {

    LOGGER.info(
        "Hello from demo-petstore-service!\nThis log message was produced at {}#getInventory",
        getClass().getCanonicalName());

    return ResponseEntity.ok().body(orderService.getInventory());
  }

  public ResponseEntity<Order> getOrderById(
      @Parameter(name = "orderId", description = "ID of order that needs to be fetched", required = true, schema = @Schema(description = "")) @PathVariable("orderId") Long orderId) {

    LOGGER.info(
        "Hello from demo-petstore-service!\nThis log message was produced at {}#getOrderById",
        getClass().getCanonicalName());

    if (orderId == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    final Order order = orderService.getOrderById(orderId);
    if (order != null) {
      return ResponseEntity.ok().body(order);
    }

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
  }

  public ResponseEntity<Order> placeOrder(
      @Parameter(name = "X-Session-ID", description = "", schema = @Schema(description = "")) @RequestHeader(value = "X-Session-ID", required = false) String xSessionID,
      @Parameter(name = "Order", description = "", schema = @Schema(description = "")) @Valid @RequestBody(required = false) Order order) {

    LOGGER.info("Hello from demo-petstore-service!\nThis log message was produced at {}#placeOrder",
        getClass().getCanonicalName());

    if (order == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    Session session = this.sessionService.getSessionById(xSessionID);
    if (session == null) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
    }

    Pet pet = this.petService.getPetById(order.getPetId());
    if (pet == null) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }

    pet.setStatus(Pet.StatusEnum.PENDING);
    this.petService.updatePet(pet);
    return ResponseEntity.ok().body(orderService.placeOrder(order));
  }
}
