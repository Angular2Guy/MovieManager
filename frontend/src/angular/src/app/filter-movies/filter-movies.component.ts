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
import {
  Component,
  DestroyRef,
  OnInit,
  inject,
  ChangeDetectionStrategy,
  signal,
} from "@angular/core";
import { Router } from "@angular/router";
import {
  NgbDatepickerModule,
  NgbDateStruct,
  NgbDropdownModule,
  NgbOffcanvas,
  NgbOffcanvasModule,
  NgbPopoverModule,
  NgbRatingConfig,
  NgbRatingModule,
  OffcanvasDismissReasons,
} from "@ng-bootstrap/ng-bootstrap";
import { MovieFilterCriteria } from "../model/movie-filter-criteria";
import { Genere } from "../model/genere";
import { Movie } from "../model/movie";
import { MoviesService } from "../services/movies.service";
import { FulltextFilter, QueryParam } from "../model/common";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { Operator, SearchString } from "../model/search-string";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-filter-movies",
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    NgbOffcanvasModule,
    NgbDatepickerModule,
    NgbDropdownModule,
    NgbRatingModule,
    NgbPopoverModule,
  ],
  templateUrl: "./filter-movies.component.html",
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ["./filter-movies.component.scss"],
})
export class FilterMoviesComponent implements OnInit {
  protected filteredMovies = signal<Movie[]>([]);
  protected filtering = signal(false);
  protected selectedGeneres = signal<Genere[]>([]);
  protected selectedGeneresStr = signal("");
  protected generes = signal<Genere[]>([]);
  protected closeResult = signal("");
  protected ngbReleaseFrom = signal<NgbDateStruct | null>(null);
  protected ngbReleaseTo = signal<NgbDateStruct | null>(null);
  protected FullTextFilter = FulltextFilter;
  protected filterType = signal(FulltextFilter.PhraseFilter);
  protected movieTitle = signal("");
  protected movieActor = signal("");
  protected minLength = signal(0);
  protected maxLength = signal(0);
  protected minRating = signal(0);
  protected phrase = signal("");
  protected otherWordsInPhrase = signal(0);
  protected searchWords = "";
  private readonly destroy: DestroyRef = inject(DestroyRef);

  constructor(
    private offcanvasService: NgbOffcanvas,
    public ngbRatingConfig: NgbRatingConfig,
    private movieService: MoviesService,
    private router: Router,
  ) {}

  public ngOnInit(): void {
    this.ngbRatingConfig.max = 10;
    this.movieService
      .allGeneres()
      .pipe(takeUntilDestroyed(this.destroy))
      .subscribe({
        next: (myGeneres) => this.generes.set(myGeneres),
        error: (failed) => this.router.navigate(["/"]),
      });
  }

  public open(content: unknown) {
    this.otherWordsInPhrase.set(0);
    this.offcanvasService
      .open(content, { ariaLabelledBy: "offcanvas-basic-title" })
      .result.then(
        (result) => {
          this.closeResult.set(`Closed with: ${result}`);
        },
        (reason) => {
          this.closeResult.set(`Dismissed ${this.getDismissReason(reason)}`);
        },
      );
  }

  public switchFilters(): void {
    this.phrase.set("");
    this.otherWordsInPhrase.set(0);
    this.filterType.set(
      this.filterType() === this.FullTextFilter.PhraseFilter
        ? this.FullTextFilter.WordFilter
        : this.FullTextFilter.PhraseFilter,
    );
  }

  public back(): void {
    this.router.navigate(["search"]);
  }

  private getDismissReason(reason: unknown): void {
    if (reason === OffcanvasDismissReasons.ESC) {
      return this.resetFilters();
    } else if (!this.hasActiveFilter()) {
      this.filteredMovies.set([]);
      return;
    } else {
      this.filtering.set(true);
      const criteria = this.buildCriteria();
      this.movieService
        .findMoviesByCriteria(criteria)
        .pipe(takeUntilDestroyed(this.destroy))
        .subscribe({
          next: (result) => {
            this.filteredMovies.set(result);
            this.filtering.set(false);
          },
          error: (failed) => {
            console.log(failed);
            this.filtering.set(false);
            this.router.navigate(["/"]);
          },
        });
    }
  }

  private hasActiveFilter(): boolean {
    return (
      this.movieTitle().length > 0 ||
      this.movieActor().length > 0 ||
      this.minLength() !== 0 ||
      this.maxLength() !== 0 ||
      this.minRating() !== 0 ||
      this.selectedGeneres().length > 0 ||
      this.ngbReleaseFrom() !== null ||
      this.ngbReleaseTo() !== null ||
      this.phrase().length > 0 ||
      this.otherWordsInPhrase() !== 0 ||
      this.searchWords.length > 0
    );
  }

  private buildCriteria(): MovieFilterCriteria {
    const criteria = new MovieFilterCriteria();
    criteria.movieTitle = this.movieTitle();
    criteria.movieActor = this.movieActor();
    criteria.minLength = this.minLength();
    criteria.maxLength = this.maxLength();
    criteria.minRating = this.minRating();
    criteria.selectedGeneres = [...this.selectedGeneres()];
    criteria.releaseFrom = !this.ngbReleaseFrom()
      ? null
      : new Date(
          this.ngbReleaseFrom()!.year,
          this.ngbReleaseFrom()!.month,
          this.ngbReleaseFrom()!.day,
        );
    criteria.releaseTo = !this.ngbReleaseTo()
      ? null
      : new Date(
          this.ngbReleaseTo()!.year,
          this.ngbReleaseTo()!.month,
          this.ngbReleaseTo()!.day,
        );
    criteria.searchTerm.searchPhrase.phrase = this.phrase();
    criteria.searchTerm.searchPhrase.otherWordsInPhrase =
      this.otherWordsInPhrase();
    criteria.searchTerm.searchStrings = this.createSearchStrings();
    return criteria;
  }

  private createSearchStrings(): SearchString[] {
    const opsMap = new Map([
      [Operator.AND.toString(), Operator.AND],
      [Operator.NOT.toString(), Operator.NOT],
      [Operator.OR.toString(), Operator.OR],
    ]);
    const searchWords = this.searchWords
      .split(" ")
      .map((str) => str.trim())
      .filter((str) => !!opsMap.get(str[0]) && str.length > 4)
      .map(
        (str) => new SearchString(str.substring(1).trim(), opsMap.get(str[0])),
      );
    return searchWords;
  }

  public addToSelectedGenere(genere: Genere): void {
    if (
      this.selectedGeneres().length < 2 &&
      this.selectedGeneres().filter((myGen) => genere.id === myGen.id)
        .length === 0
    ) {
      this.selectedGeneres.set([...this.selectedGeneres(), genere]);
      this.selectedGeneresStr.set(
        `${this.selectedGeneresStr()} ${genere.name}`.trim(),
      );
    }
  }

  public resetSelectedGeneres(): void {
    this.selectedGeneres.set([]);
    this.selectedGeneresStr.set("");
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
    this.movieTitle.set("");
    this.movieActor.set("");
    this.minLength.set(0);
    this.maxLength.set(0);
    this.minRating.set(0);
    this.phrase.set("");
    this.otherWordsInPhrase.set(0);
    this.selectedGeneres.set([]);
    this.selectedGeneresStr.set("");
    this.closeResult.set("");
    this.generes.set([]);
    this.searchWords = "";
  }
}
