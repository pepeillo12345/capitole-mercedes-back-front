import {DataTableColumnInterface} from '../../shared/models/data-table-column.interface';

//To ensure the entities are typed correctly, we define a type for the entity names.
export type EntityType = 'People' | 'Planets';

export const tableConfigs: Record<EntityType, { columns: DataTableColumnInterface[] }> = {
  People: {
    columns: [
      { field: 'name', header: 'Name' },
      { field: 'gender', header: 'Gender' },
      { field: 'birthYear', header: 'Birth Year' }
    ]
  },
  Planets: {
    columns: [
      { field: 'name', header: 'Name' },
      { field: 'climate', header: 'Climate' },
      { field: 'terrain', header: 'Terrain' },
      { field: 'population', header: 'Population' },
      { field: 'created', header: 'Created', pipe: 'date', pipeArgs: 'short' }
    ]
  }
};
