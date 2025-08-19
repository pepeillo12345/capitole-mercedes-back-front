import {HttpClient, HttpParams} from "@angular/common/http";
import {SortOption} from "../models/sort-option.interface";
import {Observable} from 'rxjs';
import {Page} from '../models/page.interface';

export abstract class BaseQueryBuilder<T> {
  protected searchTerm?: string;
  protected sorts: SortOption[] = [];
  protected pageNumber?: number;
  protected pageSize?: number;

  protected constructor(
    protected http: HttpClient,
    protected endpoint: string
  ) {}

  /**
   * Añade un término de búsqueda
   */
  search(term: string): this {
    this.searchTerm = term?.trim() || undefined;
    return this;
  }

  /**
   * Añade un criterio de ordenamiento
   */
  sort(field: string, direction: 'asc' | 'desc' = 'asc'): this {
    this.sorts = [{ field, direction }];
    return this;
  }


  /**
   * Configura la paginación
   */
  page(pageNumber: number, pageSize?: number): this {
    this.pageNumber = pageNumber;
    if (pageSize !== undefined) {
      this.pageSize = pageSize;
    }
    return this;
  }

  /**
   * Configura el tamaño de página
   */
  size(pageSize: number): this {
    this.pageSize = pageSize;
    return this;
  }


  /**
   * Resets all query parameters to their initial state
   */
  reset(): this {
    this.searchTerm = undefined;
    this.sorts = [];
    this.pageNumber = undefined;
    this.pageSize = undefined;
    return this;
  }

  /**
   * Builds the HttpParams object based on the current state
   */
  private buildParams(): HttpParams {
    let params = new HttpParams();

    if (this.searchTerm && this.searchTerm.length > 0) {
      params = params.set('search', this.searchTerm);
    }

    if (this.sorts.length > 0) {
      this.sorts.forEach(sort => {
        params = params.append('sort', `${sort.field},${sort.direction}`);
      });
    }

    if (this.pageNumber !== undefined) {
      params = params.set('page', this.pageNumber.toString());
    }

    if (this.pageSize !== undefined) {
      params = params.set('size', this.pageSize.toString());
    }

    console.log('Built params:', params.toString()); // Debug
    return params;
  }

  /**
   * Executes the HTTP GET request with the built parameters
   */
  execute(): Observable<Page<T>> {
    const params = this.buildParams();
    const url = `${this.endpoint}${params.toString() ? '?' + params.toString() : ''}`;
    console.log('Executing request to:', url); // Debug

    return this.http.get<Page<T>>(this.endpoint, { params });
  }

}
