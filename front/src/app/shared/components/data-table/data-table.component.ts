import {
  Component, computed, contentChild,
  ContentChild,
  DestroyRef, effect,
  inject, input,
  OnDestroy,
  OnInit, output,
  Output,
  signal,
  TemplateRef
} from '@angular/core';
import {NgIf, NgTemplateOutlet} from '@angular/common';
import {Input} from 'postcss';
import {DataTableConfigInterface} from '../../models/data-table-config.interface';
import {DataTableColumnInterface} from '../../models/data-table-column.interface';
import {debounceTime, distinctUntilChanged, Observable, startWith, Subject, switchMap} from 'rxjs';
import {Page} from '../../models/page.interface';
import EventEmitter = require('node:events');
import {takeUntilDestroyed} from '@angular/core/rxjs-interop';
import {QueryParams} from '../../models/query-params.interface';
import {FloatLabel} from 'primeng/floatlabel';
import {IconField} from 'primeng/iconfield';
import {InputIcon} from 'primeng/inputicon';
import {FormsModule} from '@angular/forms';
import {TableModule} from 'primeng/table';
import {Paginator} from 'primeng/paginator';
import {InputText} from 'primeng/inputtext';

@Component({
  selector: 'app-data-table',
  imports: [
    FloatLabel,
    IconField,
    InputIcon,
    FormsModule,
    TableModule,
    Paginator,
    NgTemplateOutlet,
    InputText

  ],
  templateUrl: './data-table.component.html',
  styleUrl: './data-table.scss'
})
export class DataTableComponent<T> implements OnInit{
  private destroyRef = inject(DestroyRef);

  // Inputs (Angular v20)
  config = input.required<DataTableConfigInterface>();
  columns = input.required<DataTableColumnInterface[]>();
  queryFn = input.required<(params: QueryParams) => Observable<Page<T>>>();

  bodyTemplate = contentChild<TemplateRef<any>>('bodyTemplate');

  dataLoaded = output<T[]>();
  errorOccurred = output<any>();

  data = signal<T[]>([]);
  loading = signal(false);
  first = signal(0);
  rows = signal(15);
  totalRecords = signal(0);
  searchTerm = signal('');
  sortField = signal('name');
  sortOrder = signal<'asc' | 'desc'>('asc');

  // Subject para búsqueda reactiva
  private searchSubject = new Subject<string>();

  // Computed: página actual
  currentPage = computed(() => Math.floor(this.first() / this.rows()));

  // Effect para inicializar config por defecto
  constructor() {
    effect(() => {
      const cfg = this.config();
      if (cfg) {
        this.rows.set(cfg.defaultPageSize || 15);
        this.sortField.set(cfg.defaultSortField || 'name');
        this.sortOrder.set(cfg.defaultSortOrder || 'asc');
      }
    });
  }

  ngOnInit(): void {
    this.setupSearch();
    this.fetchData();
  }

  private setupSearch(): void {
    this.searchSubject
      .pipe(
        startWith(''),
        debounceTime(500),
        distinctUntilChanged(),
        switchMap(searchTerm => {
          this.loading.set(true);
          this.first.set(0);
          return this.queryFn()({
            search: searchTerm,
            sortField: this.sortField(),
            sortOrder: this.sortOrder(),
            page: 0,
            size: this.rows()
          });
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe({
        next: (response: Page<T>) => this.handleDataResponse(response),
        error: (error) => {
          console.error(`Error loading ${this.config().entityName}:`, error);
          this.loading.set(false);
          this.errorOccurred.emit(error);
        }
      });
  }

  private handleDataResponse(response: Page<T>): void {
    this.data.set(response.content);
    this.totalRecords.set(response.totalElements);
    this.loading.set(false);
    this.dataLoaded.emit(response.content);
  }

  private fetchData(): void {
    this.loading.set(true);

    this.queryFn()({
      search: this.searchTerm(),
      sortField: this.sortField(),
      sortOrder: this.sortOrder(),
      page: this.currentPage(),
      size: this.rows()
    })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (response: Page<T>) => this.handleDataResponse(response),
        error: (error) => {
          console.error(`Error loading ${this.config().entityName}:`, error);
          this.loading.set(false);
          this.errorOccurred.emit(error);
        }
      });
  }

  onPageChange(event: any): void {
    this.first.set(event.first);
    this.rows.set(event.rows);
    this.fetchData();
  }

  onSearchChange(value: string): void {
    this.searchTerm.set(value);
    this.searchSubject.next(value);
  }

  clearSearch(): void {
    this.searchTerm.set('');
    this.searchSubject.next('');
  }

  onSort(field: string): void {
    if (this.sortField() === field) {
      this.sortOrder.set(this.sortOrder() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortField.set(field);
      this.sortOrder.set('asc');
    }
    this.first.set(0);
    this.fetchData();
  }

  getCurrentPageReportTemplate(): string {
    return `Showing {first} to {last} of {totalRecords} ${this.config().entityName}`;
  }
}
