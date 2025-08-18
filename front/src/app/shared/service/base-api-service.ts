import { HttpClient } from "@angular/common/http";
import { BaseQueryBuilder } from "../utils/base-query-builder";
import {Observable} from 'rxjs';
import {Page} from '../models/page.interface';
import {Injectable} from '@angular/core';

@Injectable()
export abstract class BaseApiService<T, TBuilder extends BaseQueryBuilder<T>> {
  protected readonly baseUrl = 'http://localhost:8080/api/v1';

  constructor(protected http: HttpClient) {}

  /**
   * Método abstracto que debe implementar cada servicio específico
   */
  abstract query(): TBuilder;

  /**
   * Métodos de conveniencia comunes
   */
  getAll(): Observable<Page<T>> {
    return this.query().execute();
  }

  search(term: string): Observable<Page<T>> {
    return this.query().search(term).execute();
  }

  getPage(page: number, size?: number): Observable<Page<T>> {
    const builder = this.query().page(page);
    return size ? builder.size(size).execute() : builder.execute();
  }
}
