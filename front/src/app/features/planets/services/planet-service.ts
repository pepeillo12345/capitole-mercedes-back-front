import { Injectable } from '@angular/core';
import { BaseApiService } from '../../../shared/service/base-api-service';
import {PlanetInterface} from '../models/planet.interface';
import {PlanetQueryBuilder} from './planet-query-builder';

@Injectable({
  providedIn: 'root'
})
export class PlanetService extends BaseApiService<PlanetInterface, PlanetQueryBuilder> {

  query(): PlanetQueryBuilder {
    return new PlanetQueryBuilder(this.http, this.baseUrl);
  }
}
