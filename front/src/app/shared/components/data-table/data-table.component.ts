import {
  Component,
  computed,
  contentChild,
  DestroyRef,
  effect,
  inject,
  input,
  OnInit,
  output,
  signal,
  TemplateRef
} from '@angular/core';
import {NgTemplateOutlet} from '@angular/common';
import {DataTableConfigInterface} from '../../models/data-table-config.interface';
import {DataTableColumnInterface} from '../../models/data-table-column.interface';
import {debounceTime, distinctUntilChanged, Observable, startWith, Subject, switchMap} from 'rxjs';
import {Page} from '../../models/page.interface';
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
export class DataTableComponent<T> implements OnInit {
  private destroyRef = inject(DestroyRef);

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

  constructor() {
    // Effect para inicializar config por defecto
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

  /**
   * Método público para resetear estado y recargar datos
   * Se llama desde el componente padre cuando cambia el topic
   */
  resetAndReload(): void {
    console.log('resetAndReload called');
    this.first.set(0);
    this.searchTerm.set('');
    this.data.set([]);
    this.fetchData();
    this.searchSubject.next('');
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
    this.totalRecords.set(response.page.totalElements); // ← Cambio aquí
    this.loading.set(false);
    this.dataLoaded.emit(response.content);
  }


  private fetchData(): void {
    console.log('Fetching data...');
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
    console.log('Ha cambiado la page')
    this.first.set(event.first);
    this.rows.set(event.rows);
    this.fetchData();
  }

  onSearchChange(value: string): void {
    this.searchTerm.set(value);
    this.searchSubject.next(value);
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
