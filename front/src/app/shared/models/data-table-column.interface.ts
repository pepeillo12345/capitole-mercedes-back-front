export interface DataTableColumnInterface {
  field: string;
  header: string;
  sortable?: boolean;
  pipe?: 'date' | 'currency' | 'number';
  pipeArgs?: any;
}
