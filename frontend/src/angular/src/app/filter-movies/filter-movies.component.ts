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
import { Component, DestroyRef, OnInit, inject } from "@angular/core";
import { Router } from "@angular/router";
import {
  NgbDateStruct,
  NgbOffcanvas,
  NgbRatingConfig,
  OffcanvasDismissReasons,
} from "@ng-bootstrap/ng-bootstrap";
import { MovieFilterCriteria } from "../model/movie-filter-criteria";
import { Genere } from "../model/genere";
import { Movie } from "../model/movie";
import { MoviesService } from "../services/movies.service";
import { QueryParam } from "../model/common";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { SearchPhrase } from "../model/search-phrase";
import { SearchTerm } from "../model/search-term";
import { Operator, SearchString } from "../model/search-string";

enum FulltextFilter {
	PhraseFilter = "phraseFilter",
	WordFilter = "wordFilter"
}

@Component({
  selector: "app-filter-movies",
  templateUrl: "./filter-movies.component.html",
  styleUrls: ["./filter-movies.component.scss"],
})
export class FilterMoviesComponent implements OnInit {
  protected filteredMovies: Movie[] = [];
  protected filtering = false;
  protected selectedGeneresStr = "";
  protected generes: Genere[] = [];
  protected closeResult = "";
  protected filterCriteria = new MovieFilterCriteria();
  protected ngbReleaseFrom: NgbDateStruct;
  protected ngbReleaseTo: NgbDateStruct;
  protected FullTextFilter = FulltextFilter;
  protected filterType = FulltextFilter.PhraseFilter;
  protected searchWords = '';
  private readonly destroy: DestroyRef = inject(DestroyRef);

  constructor(
    private offcanvasService: NgbOffcanvas,
    public ngbRatingConfig: NgbRatingConfig,
    private movieService: MoviesService,
    private router: Router
  ) {}

  public ngOnInit(): void {
    this.ngbRatingConfig.max = 10;
    this.movieService
      .allGeneres()
      .pipe(takeUntilDestroyed(this.destroy))
      .subscribe({
        next: (myGeneres) => (this.generes = myGeneres),
        error: (failed) => this.router.navigate(["/"]),
      });
  }

  public open(content: unknown) {
    this.filterCriteria.searchTerm.searchPhrase.otherWordsInPhrase = null;
    this.offcanvasService
      .open(content, { ariaLabelledBy: "offcanvas-basic-title" })
      .result.then(
        (result) => {
          this.closeResult = `Closed with: ${result}`;
        },
        (reason) => {
          this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
        }
      );
  }

  public switchFilters(): void {
	  this.filterCriteria.searchTerm = new SearchTerm();
	  this.filterType = this.filterType === this.FullTextFilter.PhraseFilter ? this.FullTextFilter.WordFilter : this.FullTextFilter.PhraseFilter;	  
  }

  public back(): void {
    this.router.navigate(["search"]);
  }

  private getDismissReason(reason: unknown): void {
    //console.log(this.filterCriteria);
    if (reason === OffcanvasDismissReasons.ESC) {
      return this.resetFilters();
    } else {
      this.filterCriteria.releaseFrom = !this.ngbReleaseFrom
        ? null
        : new Date(
            this.ngbReleaseFrom.year,
            this.ngbReleaseFrom.month,
            this.ngbReleaseFrom.day
          );
      this.filterCriteria.releaseTo = !this.ngbReleaseTo
        ? null
        : new Date(
            this.ngbReleaseTo.year,
            this.ngbReleaseTo.month,
            this.ngbReleaseTo.day
          );
      this.filterCriteria.searchTerm.searchPhrase.otherWordsInPhrase = !this.filterCriteria.searchTerm
        .searchPhrase.otherWordsInPhrase
        ? 0
        : this.filterCriteria.searchTerm.searchPhrase.otherWordsInPhrase;
      this.filterCriteria.searchTerm.searchStrings = this.createSearchStrings();
      this.movieService.findMoviesByCriteria(this.filterCriteria).pipe(takeUntilDestroyed(this.destroy)).subscribe({
        next: (result) => (this.filteredMovies = result),
        error: (failed) => {
          console.log(failed);
          this.router.navigate(["/"]);
        },
      });
    }
  }

  private createSearchStrings(): SearchString[] {
	  const opsMap = new Map([[Operator.AND.toString(), Operator.AND], [Operator.NOT.toString(), Operator.NOT], [Operator.OR.toString(), Operator.OR]]);	 
	  //console.log(this.searchWords.split(' ')); 
	  const searchWords = this.searchWords.split(' ').map(str => str.trim())
	    .filter(str => !!opsMap.get(str[0]) && str.length > 4).map(str => new SearchString(str.substring(1).trim(), opsMap.get(str[0])));	  
	  return searchWords;
  }

  public addToSelectedGenere(genere: Genere): void {
    if (
      this.filterCriteria.selectedGeneres.length < 2 &&
      this.filterCriteria.selectedGeneres.filter(
        (myGen) => genere.id === myGen.id
      ).length === 0
    ) {
      this.selectedGeneresStr = `${this.selectedGeneresStr} ${genere.name}`;
      this.selectedGeneresStr = this.selectedGeneresStr.trim();
      this.filterCriteria.selectedGeneres.push(genere);
    }
  }

  public resetSelectedGeneres(): void {
    this.filterCriteria.selectedGeneres = [];
    this.selectedGeneresStr = "";
  }

  public selectMovie(movie: Movie): void {
    this.router.navigate(["movie", movie.id], {
      queryParams: { back: QueryParam.MoviesBack },
    });
  }

  public showFilterActors(): void {
    this.router.navigate(["/filter-actors"]);
  }

  public resetFilters(): void {
    this.filterCriteria.releaseFrom = null;
    this.filterCriteria.releaseTo = null;
    this.filterCriteria.selectedGeneres = [];
    this.selectedGeneresStr = "";
    this.closeResult = "";
    this.generes = [];
    this.filterCriteria.movieTitle = "";
    this.filterCriteria.movieActor = "";
    this.filterCriteria.minLength = 0;
    this.filterCriteria.maxLength = 0;
    this.filterCriteria.minRating = 0;
    this.filterCriteria.searchTerm.searchPhrase.phrase = '';
    this.filterCriteria.searchTerm.searchPhrase.otherWordsInPhrase = null;
    this.filterCriteria.searchTerm.searchStrings = [];    
  }
}
