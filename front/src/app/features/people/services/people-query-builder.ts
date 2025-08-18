import {BaseQueryBuilder} from '../../../shared/utils/base-query-builder';
import {HttpClient} from '@angular/common/http';
import {PeopleInterface} from '../models/people.interface';

export class PeopleQueryBuilder extends BaseQueryBuilder<PeopleInterface> {
  constructor(http: HttpClient, baseUrl: string) {
    super(http, `${baseUrl}/people`);
  }
}
