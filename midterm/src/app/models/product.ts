import { BrandResponse } from "./brand";
import { CategoryResponse } from "./category";

export interface ProductResponse {
    id: number;
    name: string;
    description: string;
    price: number;
    image: string;
    stock: number;
    discount: number;
    brand: BrandResponse;
    category: CategoryResponse;
}

export interface ProductRequest {
    name: string;
    description: string;
    price: number;
    stock: number;
    discount: number;
    brandId: number;
    categoryId: number;
}

export interface ProductFilter {
    brandId: number | null;
    categoryId: number | null;
    keyword: string;
    sort: string;
    page: number;
}

export const ProductSortOptions = [
    { value: 'HIGHEST_COST', label: 'Giá cao nhất' },
    { value: 'LOWEST_COST', label: 'Giá thấp nhất' },
    { value: 'NEWEST', label: 'Mới nhất' }
];