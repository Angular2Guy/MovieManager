import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ActorsComponent } from './actors.component';

describe('ActorsComponent', () => {
  let component: ActorsComponent;
  let fixture: ComponentFixture<ActorsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ActorsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ActorsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
