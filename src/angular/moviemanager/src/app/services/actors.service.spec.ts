import { TestBed, inject } from '@angular/core/testing';

import { ActorsService } from './actors.service';

describe('ActorsService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [ActorsService]
    });
  });

  it('should be created', inject([ActorsService], (service: ActorsService) => {
    expect(service).toBeTruthy();
  }));
});
