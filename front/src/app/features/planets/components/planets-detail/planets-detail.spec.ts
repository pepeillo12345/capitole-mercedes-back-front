import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PlanetsDetail } from './planets-detail';

describe('PlanetsDetail', () => {
  let component: PlanetsDetail;
  let fixture: ComponentFixture<PlanetsDetail>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlanetsDetail]
    })
    .compileComponents();

    fixture = TestBed.createComponent(PlanetsDetail);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
