package com.pht.dto;

import java.util.Collection;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class CustomPaging<T> {
	private Collection<T> contents;
	private long totalPages;
	private long currentPage;
	private long pageSize;

	public CustomPaging(Page<T> pages) {
		this.contents = pages.getContent();
		this.totalPages = pages.getTotalPages();
		this.currentPage = pages.getNumber();
		this.pageSize = pages.getSize();
	}

	public <E> CustomPaging(Collection<E> contents, Function<E, T> mapper, Pageable pageable) {
		this.contents = contents.stream().skip(pageSize * currentPage).limit(pageSize).map(mapper).toList();
		this.pageSize = pageable.getPageSize();
		this.currentPage = pageable.getPageNumber();
		this.totalPages = (long) Math.ceil((double) contents.size() / pageSize);
	}

	public <E> CustomPaging(Page<E> pages, Function<E, T> mapper) {
		this.contents = pages.getContent().stream().map(mapper).toList();
		this.totalPages = pages.getTotalPages();
		this.currentPage = pages.getNumber();
		this.pageSize = pages.getSize();
	}
}