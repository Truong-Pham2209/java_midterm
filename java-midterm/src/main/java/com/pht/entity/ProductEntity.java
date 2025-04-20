package com.pht.entity;

import java.time.LocalDate;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "products")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	long id;
	
	String name;
	
	@Column(columnDefinition = "TEXT")
	String description;
	
	long price;
	
	UUID image;
	
	long stock;
	
	int discount;
	
	boolean isDeleted;
	
	@CreationTimestamp
	LocalDate createdAt;
	
	@UpdateTimestamp
	LocalDate updatedAt;
	
	@ManyToOne
	@JoinColumn(name = "category_id")
	CategoryEntity category;
	
	@ManyToOne
	@JoinColumn(name = "brand_id")
	BrandEntity brand;
}
