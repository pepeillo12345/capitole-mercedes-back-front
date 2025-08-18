import { Injectable } from '@angular/core';
import {BaseApiService} from '../../../shared/service/base-api-service';
import {PeopleInterface} from '../models/people.interface';
import {PeopleQueryBuilder} from './people-query-builder';

@Injectable({
  providedIn: 'root'
})
export class PeopleService extends BaseApiService<PeopleInterface, PeopleQueryBuilder>{
  query(): PeopleQueryBuilder {
    return new PeopleQueryBuilder(this.http, this.baseUrl);
  }
}
