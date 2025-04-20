package com.pht.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pht.entity.ProductEntity;

@Repository
public interface ProductRepo extends JpaRepository<ProductEntity, Long> {
	boolean existsByBrandIdAndIsDeletedIsFalse(long brandId);

	boolean existsByCategoryIdAndIsDeletedIsFalse(long categoryId);

	@Query("""
			    SELECT p FROM ProductEntity p
			    WHERE p.isDeleted = false
			    AND (:brandId IS NULL OR p.brand.id = :brandId)
			    AND (:categoryId IS NULL OR p.category.id = :categoryId)
			    AND (
			        :keyword IS NULL OR
			        LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
			        LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))
			    )
			""")
	Page<ProductEntity> getByFilter(Long brandId, Long categoryId, String keyword, Pageable pageable);
}