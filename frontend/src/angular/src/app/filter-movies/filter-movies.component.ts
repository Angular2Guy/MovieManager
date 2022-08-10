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
import { NgbOffcanvas, NgbRatingConfig, OffcanvasDismissReasons } from '@ng-bootstrap/ng-bootstrap';
import { Movie } from '../model/movie';

@Component({
  selector: 'app-filter-movies',
  templateUrl: './filter-movies.component.html',
  styleUrls: ['./filter-movies.component.scss']
})
export class FilterMoviesComponent implements OnInit {
  public filteredMovies: Movie[] = [];
  public filtering = false;
  public selectedGeneres = '';
  public closeResult = '';
  
  constructor(private offcanvasService: NgbOffcanvas, public ngbRatingConfig: NgbRatingConfig) {}
  
  ngOnInit(): void {
     this.ngbRatingConfig.max = 10;
  }

  public open(content: any) {
    this.offcanvasService.open(content, {ariaLabelledBy: 'offcanvas-basic-title'}).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  private getDismissReason(reason: any): string {
    if (reason === OffcanvasDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === OffcanvasDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on the backdrop';
    } else {
      return `with: ${reason}`;
    }
  }

  public selectMovie(movie: Movie): void {
	
  }
}
