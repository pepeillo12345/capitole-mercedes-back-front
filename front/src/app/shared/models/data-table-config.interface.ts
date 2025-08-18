export interface DataTableConfigInterface {
  entityName: string;
  searchPlaceholder?: string;
  defaultPageSize?: number;
  pageSizeOptions?: number[];
  defaultSortField?: string;
  defaultSortOrder?: 'asc' | 'desc';
}
