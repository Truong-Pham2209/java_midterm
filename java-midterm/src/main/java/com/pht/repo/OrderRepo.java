package com.pht.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pht.entity.OrderEntity;

@Repository
public interface OrderRepo extends JpaRepository<OrderEntity, Long> {
	@Query("""
				SELECT o FROM OrderEntity o
				WHERE o.product.isDeleted = false
				AND o.user.username = :username
				AND inCart = true
			""")
	List<OrderEntity> getAllOrderInCart(String username);

	@Query("""
				SELECT o FROM OrderEntity o
				WHERE o.user.username = :username
				AND inCart = false
			""")
	Page<OrderEntity> getHistoryOrder(String username, Pageable pageable);

	@Query("""
				SELECT COUNT(o) > 0 FROM OrderEntity o
				WHERE o.product.id = :productId
				AND o.user.id = :userId
				AND inCart = true
			""")
	boolean isProductInCart(long productId, long userId);
}