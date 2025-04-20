export interface Paging<T> {
    contents: T[];
    totalPages: number;
    currentPage: number;
}