package com.pht.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.pht.dto.CustomPaging;
import com.pht.dto.OrderItem;
import com.pht.dto.request.ConfirmedOrderRequest;
import com.pht.dto.request.OrderRequest;
import com.pht.dto.response.OrderResponse;
import com.pht.entity.OrderEntity;
import com.pht.entity.ProductEntity;
import com.pht.entity.UserEntity;
import com.pht.repo.OrderRepo;
import com.pht.repo.ProductRepo;
import com.pht.repo.UserRepo;
import com.pht.service.impl.OrderServiceImpl;
import com.pht.util.AuditingUtil;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    OrderRepo orderRepo;
    @Mock
    UserRepo userRepo;
    @Mock
    ProductRepo productRepo;

    @InjectMocks
    OrderServiceImpl orderService;

    @Test
    void testGetAllInCart_ShouldReturnListOrderResponse() {
        String username = "testUser";
        UUID image = UUID.randomUUID();
        UserEntity user = new UserEntity();
        ProductEntity product = ProductEntity.builder().id(1L).price(100).name("Product").discount(10).image(image)
                .build();
        OrderEntity order = OrderEntity.builder().id(1L).product(product).user(user).inCart(true).quantity(2).build();

        when(orderRepo.getAllOrderInCart(username)).thenReturn(List.of(order));

        try (MockedStatic<AuditingUtil> mockStatic = mockStatic(AuditingUtil.class)) {
            mockStatic.when(AuditingUtil::getCurrentUser).thenReturn(username);

            List<OrderResponse> responses = orderService.getAllInCart();
            assertEquals(1, responses.size());
            assertEquals("Product", responses.get(0).getProductName());
            assertEquals(100, responses.get(0).getPrice());
        }
    }

    @Test
    void testGetOrderHistory_ShouldReturnPaging() {
        String username = "testUser";
        Pageable pageable = PageRequest.of(0, 10);
        UUID image = UUID.randomUUID();
        ProductEntity product = ProductEntity.builder().id(1L).price(200).name("Product").discount(15).image(image)
                .build();
        OrderEntity order = OrderEntity.builder().id(2L).product(product).price(170).quantity(1).build();
        Page<OrderEntity> page = new PageImpl<>(List.of(order), pageable, 1);

        when(orderRepo.getHistoryOrder(username, pageable)).thenReturn(page);

        try (MockedStatic<AuditingUtil> mocked = mockStatic(AuditingUtil.class)) {
            mocked.when(AuditingUtil::getCurrentUser).thenReturn(username);
            CustomPaging<OrderResponse> result = orderService.getOrderHistory(0);
            assertEquals(1, result.getContents().size());
            assertEquals(170, result.getContents().iterator().next().getPrice());
        }
    }

    @Test
    void testAddToCart_ShouldSave_WhenValid() {
        String username = "testUser";
        UUID image = UUID.randomUUID();
        ProductEntity product = ProductEntity.builder().id(1L).price(100).isDeleted(false).image(image).build();
        UserEntity user = new UserEntity();
        user.setId(1L);
        OrderRequest request = new OrderRequest();
        request.setProductId(1L);

        when(userRepo.findByUsername(username)).thenReturn(Optional.of(user));
        when(productRepo.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepo.isProductInCart(1L, 1L)).thenReturn(false);

        try (MockedStatic<AuditingUtil> mocked = mockStatic(AuditingUtil.class)) {
            mocked.when(AuditingUtil::getCurrentUser).thenReturn(username);
            orderService.addToCart(request);
            verify(orderRepo, times(1)).save(any(OrderEntity.class));
        }
    }

    @Test
    void testAddToCart_ShouldNotSave_WhenUserOrProductInvalid() {
        String username = "testUser";
        OrderRequest request = new OrderRequest();
        request.setProductId(1L);

        when(userRepo.findByUsername(username)).thenReturn(Optional.empty());

        try (MockedStatic<AuditingUtil> mocked = mockStatic(AuditingUtil.class)) {
            mocked.when(AuditingUtil::getCurrentUser).thenReturn(username);
            orderService.addToCart(request);
            verify(orderRepo, never()).save(any());
        }
    }

    @Test
    void testDelete_ShouldCallDeleteById() {
        orderService.delete(10L);
        verify(orderRepo, times(1)).deleteById(10L);
    }

    @Test
    void testConfirmedOrder_ShouldUpdateOrderCorrectly() {
        String username = "testUser";
        UUID image = UUID.randomUUID();
        ProductEntity product = ProductEntity.builder().id(1L).price(500).discount(10).image(image).name("Laptop")
                .build();
        UserEntity user = new UserEntity();
        user.setUsername(username);
        OrderEntity order = OrderEntity.builder().id(1L).inCart(true).product(product).user(user).build();
        OrderItem item = new OrderItem();
        item.setId(1L);
        item.setQuantity(2);

        ConfirmedOrderRequest request = new ConfirmedOrderRequest();
        request.setItems(List.of(item));
        request.setAddress("123 Street");

        when(orderRepo.findAllById(List.of(1L))).thenReturn(List.of(order));

        try (MockedStatic<AuditingUtil> mocked = mockStatic(AuditingUtil.class)) {
            mocked.when(AuditingUtil::getCurrentUser).thenReturn(username);
            orderService.confirmedOrder(request);
            assertEquals(2, order.getQuantity());
            assertFalse(order.isInCart());
            assertEquals("123 Street", order.getAddress());
            assertEquals(450, order.getPrice()); // 500 - 10%
            verify(orderRepo, times(1)).saveAll(List.of(order));
        }
    }

    @Test
    void testConfirmedOrder_ShouldSkipInvalidCases() {
        String username = "wrongUser";
        ProductEntity product = ProductEntity.builder().id(1L).price(500).discount(10).build();
        UserEntity user = new UserEntity();
        user.setUsername("correctUser");
        OrderEntity order = OrderEntity.builder().id(1L).inCart(true).product(product).user(user).build();
        OrderItem item = new OrderItem();
        item.setId(1L);
        item.setQuantity(2);

        ConfirmedOrderRequest request = new ConfirmedOrderRequest();
        request.setItems(List.of(item));
        request.setAddress("123 Street");

        when(orderRepo.findAllById(List.of(1L))).thenReturn(List.of(order));

        try (MockedStatic<AuditingUtil> mocked = mockStatic(AuditingUtil.class)) {
            mocked.when(AuditingUtil::getCurrentUser).thenReturn(username);
            orderService.confirmedOrder(request);
            verify(orderRepo).saveAll(List.of(order)); // vẫn gọi saveAll nhưng không thay đổi gì
            assertTrue(order.isInCart());
            assertEquals(0, order.getQuantity());
        }
    }
}
