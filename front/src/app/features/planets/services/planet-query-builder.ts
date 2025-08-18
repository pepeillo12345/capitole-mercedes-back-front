import {BaseQueryBuilder} from '../../../shared/utils/base-query-builder';
import {PlanetInterface} from '../models/planet.interface';
import {HttpClient} from '@angular/common/http';

export class PlanetQueryBuilder extends BaseQueryBuilder<PlanetInterface> {
  constructor(http: HttpClient, baseUrl: string) {
    super(http, `${baseUrl}/planets`);
  }
}
