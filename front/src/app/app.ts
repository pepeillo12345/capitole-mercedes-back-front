import {Component, effect, signal} from '@angular/core';
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

  // Signal para el topic seleccionado
  selectedTopic = signal<EntityType>('People');

  // Signal para las columnas actuales
  currentColumns = signal<DataTableColumnInterface[]>(tableConfigs['People'].columns);

  // Lista de topics para el selector
  topics: EntityType[] = Object.keys(tableConfigs) as EntityType[];

  constructor(
    private peopleService: PeopleService,
    private planetService: PlanetService
  ) {
    // Cada vez que cambia el topic, actualizamos columnas y queryFn
    effect(() => {
      const topic = this.selectedTopic();
      this.currentColumns.set(tableConfigs[topic].columns);
    });
  }

  // Actualiza el topic y las columnas
  onTopicChange(topic: 'People' | 'Planets') {
    this.selectedTopic.set(topic);
  }

  // Aquí definimos la función que devuelve un Observable con los datos
  getQueryFn(topic: EntityType) {
    // Extrae el servicio del map
    const serviceMap = {
      People: this.peopleService,
      Planets: this.planetService
    };
    const service = serviceMap[topic];

    return (params: QueryParams): Observable<Page<any>> => {
      const {search, sortField, sortOrder, page, size} = params;
      return service
        .query()
        .search(search)
        .sort(sortField, sortOrder)
        .page(page)
        .size(size)
        .execute();
    };
  }
}
