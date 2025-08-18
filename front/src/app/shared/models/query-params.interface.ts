export interface QueryParams {
  search: string;
  sortField: string;
  sortOrder: 'asc' | 'desc';
  page: number;
  size: number;
}
