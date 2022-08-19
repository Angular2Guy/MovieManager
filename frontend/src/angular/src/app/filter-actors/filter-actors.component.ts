/**
 *    Copyright 2019 Sven Loesekann
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
       http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { NgbDateStruct, NgbOffcanvas, NgbRatingConfig } from '@ng-bootstrap/ng-bootstrap';
import { Actor, Gender } from '../model/actor';
import { ActorFilterCriteria } from '../model/actor-filter-criteria';
import { ActorsService } from '../services/actors.service';

@Component({
  selector: 'app-filter-actors',
  templateUrl: './filter-actors.component.html',
  styleUrls: ['./filter-actors.component.scss']
})
export class FilterActorsComponent implements OnInit {
  public gender=Gender;
  public filtering = false;
  public filteredActors: Actor[] = [];
  public ngbReleaseFrom: NgbDateStruct;
  public ngbReleaseTo: NgbDateStruct;
  public closeResult = '';
  public filterCriteria = new ActorFilterCriteria();
  
  constructor(private actorsService: ActorsService, private router: Router,
     private offcanvasService: NgbOffcanvas, public ngbRatingConfig: NgbRatingConfig, ) { }

  public ngOnInit(): void {
     this.ngbRatingConfig.max = 10;
  }

  public open(content: unknown) {
    this.offcanvasService.open(content, {ariaLabelledBy: 'offcanvas-basic-title'}).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  public back() {
	this.router.navigate(['search']);
  }

  public selectActor(actor: Actor): void {
  
  }
  
  public resetFilters(): void {
	
  }
  
  private getDismissReason(reason: unknown): void {
	  //console.log(this.filterCriteria);
  }
}
