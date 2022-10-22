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
import { NgbDateStruct, NgbOffcanvas, NgbRatingConfig, OffcanvasDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import { Actor, Gender } from '../model/actor';
import { ActorFilterCriteria } from '../model/actor-filter-criteria';
import { QueryParam } from '../model/common';
import { ActorsService } from '../services/actors.service';

@Component({
  selector: 'app-filter-actors',
  templateUrl: './filter-actors.component.html',
  styleUrls: ['./filter-actors.component.scss']
})
export class FilterActorsComponent implements OnInit {
  protected gender = Gender;
  protected filtering = false;
  protected filteredActors: Actor[] = [];
  protected ngbBirthdayFrom: NgbDateStruct;
  protected ngbBirthdayTo: NgbDateStruct;
  protected closeResult = '';
  protected filterCriteria = new ActorFilterCriteria();
  
  constructor(private actorsService: ActorsService, private router: Router,
     private offcanvasService: NgbOffcanvas, public ngbRatingConfig: NgbRatingConfig, ) { }

  public ngOnInit(): void {
     this.ngbRatingConfig.max = 10;
  }

  public open(content: unknown) {
	this.filterCriteria.searchPhrase.otherWordsInPhrase = null;
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
    this.router.navigate(['actor', actor.id], { queryParams: { back: QueryParam.ActorsBack } });
  }
  
  public resetFilters(): void {
	this.filterCriteria.birthdayFrom = null
	this.filterCriteria.birthdayTo = null;
	this.filterCriteria.dead = false;
	this.filterCriteria.gender = Gender.Unknown;
	this.filterCriteria.movieCharacter = '';
	this.filterCriteria.name = '';
	this.filterCriteria.popularity = 0;
	this.filterCriteria.searchPhrase.otherWordsInPhrase = null;
	this.filterCriteria.searchPhrase.phrase = '';
	this.closeResult = '';
  }
  
  public showFilterMovies(): void {
	this.router.navigate(['/filter-movies']);
  }
  
  private getDismissReason(reason: unknown): void {
	  //console.log(this.filterCriteria);
	  if (reason === OffcanvasDismissReasons.ESC) {
      return this.resetFilters();
    } else {
	  this.filterCriteria.birthdayFrom = !this.ngbBirthdayFrom ? null : 
	     new Date(this.ngbBirthdayFrom.year, this.ngbBirthdayFrom.month, this.ngbBirthdayFrom.day);
	  this.filterCriteria.birthdayTo = !this.ngbBirthdayTo ? null : 
	     new Date(this.ngbBirthdayTo.year, this.ngbBirthdayTo.month, this.ngbBirthdayTo.day);
	  	  this.filterCriteria.searchPhrase.otherWordsInPhrase = !this.filterCriteria.searchPhrase.otherWordsInPhrase ? 
	    0 : this.filterCriteria.searchPhrase.otherWordsInPhrase; 
      this.actorsService.findActorsByCriteria(this.filterCriteria)
         .subscribe({next: result => this.filteredActors = result, error: failed => {
	        console.log(failed);
	        this.router.navigate(['/']);
         }
       });
    }
  }
}
