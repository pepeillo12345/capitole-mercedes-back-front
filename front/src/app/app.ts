import {Component, effect, signal, viewChild} from '@angular/core';
import {Select} from 'primeng/select';
import {TableModule} from 'primeng/table';
import {FormsModule} from '@angular/forms';
import {PageHeader} from './shared/components/page-header/page-header';
import {DataTableComponent} from './shared/components/data-table/data-table.component';
import {DataTableColumnInterface} from './shared/models/data-table-column.interface';
import {EntityType, tableConfigs} from './core/config/table-config';
import {QueryParams} from './shared/models/query-params.interface';
import {Observable} from 'rxjs';
import {Page} from './shared/models/page.interface';
import {PeopleService} from './features/people/services/people-service';
import {PlanetService} from './features/planets/services/planet-service';


@Component({
  selector: 'app-root',
  imports: [
    TableModule,
    FormsModule,
    PageHeader,
    DataTableComponent,
    Select,
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {

  // ViewChild para acceder al componente DataTable
  dataTable = viewChild<DataTableComponent<any>>('dataTable');

  // Signal para el topic seleccionado
  selectedTopic = signal<EntityType>('People');

  // Signal para las columnas actuales
  currentColumns = signal<DataTableColumnInterface[]>(tableConfigs['People'].columns);

  // Lista de topics para el selector - convertir a array con labels
  topics = Object.keys(tableConfigs).map(key => ({
    label: key,
    value: key as EntityType
  }));

  constructor(
    private peopleService: PeopleService,
    private planetService: PlanetService
  ) {
    // Effect para actualizar columnas cuando cambia el topic
    effect(() => {
      const topic = this.selectedTopic();
      this.currentColumns.set(tableConfigs[topic].columns);
      console.log('Topic changed to:', topic); // Debug

      // Forzar recarga de datos cuando cambia el topic
      // Usamos setTimeout para asegurar que el ViewChild esté disponible
      setTimeout(() => {
        const dataTableRef = this.dataTable();
        if (dataTableRef) {
          // Resetear estado y recargar datos
          dataTableRef.resetAndReload();
        }
      });
    });
  }

  // ✅ Corregido: usar EntityType en lugar de literal types
  onTopicChange(topic: EntityType) {
    console.log('onTopicChange called with:', topic); // Debug
    this.selectedTopic.set(topic);
  }

  // Función que devuelve la función de query apropiada
  getQueryFn(topic: EntityType) {
    console.log('getQueryFn called with topic:', topic); // Debug

    const serviceMap = {
      People: this.peopleService,
      Planets: this.planetService
    };

    const service = serviceMap[topic];
    if (!service) {
      console.error('No service found for topic:', topic);
      return () => new Observable<Page<any>>();
    }

    return (params: QueryParams): Observable<Page<any>> => {
      console.log('Query function called with params:', params); // Debug
      const {search, sortField, sortOrder, page, size} = params;
      return service
        .query()
        .search(search || '')
        .sort(sortField, sortOrder)
        .page(page)
        .size(size)
        .execute();
    };
  }
}
