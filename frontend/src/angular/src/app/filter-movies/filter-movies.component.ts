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
import { Genere } from '../model/genere';
import { Movie } from '../model/movie';
import { MoviesService } from '../services/movies.service';

@Component({
  selector: 'app-filter-movies',
  templateUrl: './filter-movies.component.html',
  styleUrls: ['./filter-movies.component.scss']
})
export class FilterMoviesComponent implements OnInit {
  public filteredMovies: Movie[] = [];
  public filtering = false;
  private selectedGeneres: Genere[] = [];
  public selectedGeneresStr = '';
  public closeResult = '';
  public generes: Genere[] = [];
  
  constructor(private offcanvasService: NgbOffcanvas, public ngbRatingConfig: NgbRatingConfig, 
     private movieService: MoviesService) {}
  
  ngOnInit(): void {
     this.ngbRatingConfig.max = 10;
     this.movieService.allGeneres().subscribe(myGeneres => this.generes = myGeneres);
  }

  public open(content: unknown) {
    this.offcanvasService.open(content, {ariaLabelledBy: 'offcanvas-basic-title'}).result.then((result) => {
      this.closeResult = `Closed with: ${result}`;
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  private getDismissReason(reason: unknown): string {
    if (reason === OffcanvasDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === OffcanvasDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on the backdrop';
    } else {
      return `with: ${reason}`;
    }
  }

  public addToSelectedGenere(genere: Genere): void {
	if(this.selectedGeneres.length < 2 
	   && this.selectedGeneres.filter(myGen => genere.id === myGen.id).length === 0) {
	   this.selectedGeneresStr = `${this.selectedGeneresStr} ${genere.name}`;
	   this.selectedGeneresStr = this.selectedGeneresStr.trim();
 	   this.selectedGeneres.push(genere);
	}
  }

  public resetSelectedGeneres(): void {
	this.selectedGeneres = [];
	this.selectedGeneresStr = '';
  }

  public selectMovie(movie: Movie): void {
	
  }
}
