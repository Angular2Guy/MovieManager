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
  HostListener,
  ViewChild,
  ElementRef,
  DestroyRef,
  inject,
  ChangeDetectionStrategy,
  signal,
  computed,
  effect,
  OnInit,
  Signal,
} from "@angular/core";
import { Movie } from "../model/movie";
import { Actor } from "../model/actor";
import { Genere } from "../model/genere";
import { ActorsService } from "../services/actors.service";
import { MoviesService } from "../services/movies.service";
import { iif, of, Observable } from "rxjs";
import { FormControl, FormsModule, ReactiveFormsModule } from "@angular/forms";
import {
  tap,
  debounceTime,
  distinctUntilChanged,
  switchMap,
  catchError,
  filter,
  map,
} from "rxjs/operators";
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { QueryParam } from "../model/common";
import { TokenService } from "ngx-simple-charts/base-service";
import { takeUntilDestroyed, toSignal } from "@angular/core/rxjs-interop";
import { CommonModule } from "@angular/common";
import { LoginComponent } from "../login/login.component";

@Component({
  selector: "app-search",
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    LoginComponent,
  ],
  templateUrl: "./search.component.html",
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ["./search.component.scss"],
})
export class SearchComponent implements OnInit {
  @ViewChild("movies") moviesRef!: ElementRef;
  private readonly destroy: DestroyRef = inject(DestroyRef);
  protected generes = signal<Genere[]>([]);
  protected movieTitle = new FormControl("");
  protected movies: Signal<Movie[]> = toSignal(
    this.movieTitle.valueChanges.pipe(takeUntilDestroyed(this.destroy),
      filter((title: string | null) => !!title && title.length > 2),
      map((title: string | null) => title ?? ''),
      debounceTime(400),
      distinctUntilChanged(),
      tap(() => this.moviesLoading.set(true)),
      switchMap((title: string) =>
        iif(
          () => title.length > 2,
          this.movieService
            .findMovieByTitle(title)
            .pipe(catchError((error) => this.handleRxJsProblem(error))),
          of([]),
        ),
      ),
      tap(() => this.moviesLoading.set(false)),
    ),
    { initialValue: [] },
  );
  protected movieActor = new FormControl("");
  protected actors: Signal<Actor[]> = toSignal(
    this.movieActor.valueChanges.pipe(takeUntilDestroyed(this.destroy),
      filter((name: string | null) => !!name && name.length > 2),
      map((name: string | null) => name ?? ''),
      debounceTime(400),
      distinctUntilChanged(),
      tap(() => this.actorsLoading.set(true)),
      switchMap((name: string) =>
        iif(
          () => name.length > 2,
          this.actorService
            .findActorByName(name)
            .pipe(catchError((error) => this.handleRxJsProblem(error))),
          of([]),
        ),
      ),
      tap(() => this.actorsLoading.set(false)),
    ),
    { initialValue: [] },
  );
  protected importMovies: Movie[] = [];
  protected importMovieTitle = new FormControl("");
  protected actorsLoading = signal(false);
  protected moviesLoading = signal(false);
  protected importMoviesLoading = signal(false);
  protected showMenu = signal(false);
  protected moviesByGenere = signal<Movie[]>([]);
  protected moviesByGenLoading = signal(false);
  protected scrollMovies = signal<Movie[]>([]);
  protected scMoviesPageEnd = signal(1);
  protected loading = signal(false);
  protected allMoviesLoaded = signal(false);

  constructor(
    private actorService: ActorsService,
    private movieService: MoviesService,
    private tokenService: TokenService,
    private route: ActivatedRoute,
    private router: Router,
  ) {}

  @HostListener("window:scroll", ["$event"])
  scroll($event: Event) {
    const ypos = window.pageYOffset + window.innerHeight;
    const contentHeight = !this.moviesRef
      ? 100000000
      : this.moviesRef.nativeElement.offsetHeight + this.moviesRef.nativeElement.getBoundingClientRect().y;
    console.log(ypos, contentHeight, window.innerHeight, this.moviesRef.nativeElement.getBoundingClientRect().y);
    if (ypos >= contentHeight || contentHeight < window.innerHeight) {
      this.fetchMore();
    }
  }

  importMovie() {
    const myTitle = encodeURIComponent(this.importMovieTitle.value ?? '');
    this.router.navigate(["movie-import"], {
      queryParams: { [QueryParam.MovieName]: myTitle },
    });
  }

  dropDown() {
    this.showMenu.set(!this.showMenu());
    if (this.moviesByGenere().length > 0) {
      this.showMenu.set(false);
    }
    this.moviesByGenere.set([]);
  }

  showGenere(id: number) {
    this.showMenu.set(false);
    this.moviesByGenLoading.set(true);
    this.movieService
      .findMoviesByGenereId(id)
      .pipe(takeUntilDestroyed(this.destroy))
      .subscribe((res) => {
        this.moviesByGenere.set(res);
        this.moviesByGenLoading.set(false);
      });
  }

  showFilterMovies(): void {
    this.router.navigate(["filter-movies"]);
  }

  movieDetails(movie: Movie) {
    this.router.navigateByUrl("movie/" + movie.id);
  }

  ngOnInit() {
    if (!!this.tokenService.userId) {
      this.movieService
        .allGeneres()
        .pipe(takeUntilDestroyed(this.destroy))
        .subscribe((res) => this.generes.set(res));
    }
    this.route.url.subscribe(() => {
      if (!!this.tokenService.userId) {
        this.initScrollMovies();
      }
    });
  }

  private handleRxJsProblem(error: unknown): Observable<any[]> {
    console.log(error);
    this.router.navigate(["/"]);
    return of([]);
  }

  fetchMore() {
    if (this.allMoviesLoaded() || this.loading()) {
      return;
    }
    this.loading.set(true);
    this.movieService
      .findMoviesByPage(this.scMoviesPageEnd())
      .pipe(
        catchError((error) => this.handleRxJsProblem(error)),
        takeUntilDestroyed(this.destroy),
      )
      .subscribe((res) => {
        if (res.length > 0) {
          this.scrollMovies.set(this.scrollMovies().concat(res));
          this.scMoviesPageEnd.set(this.scMoviesPageEnd() + 1);
        } else {
          this.allMoviesLoaded.set(true);
        }
        this.loading.set(false);
      });
  }

  loginClosed(closed: boolean) {
    if (!!this.tokenService.userId && closed) {
      this.movieService
        .allGeneres()
        .pipe(takeUntilDestroyed(this.destroy))
        .subscribe((res) => this.generes.set(res));
      this.initScrollMovies();
    }
  }

  private initScrollMovies() {
    this.loading.set(false);
    this.allMoviesLoaded.set(false);
    this.movieService
      .findMoviesByPage(this.scMoviesPageEnd())
      .pipe(takeUntilDestroyed(this.destroy))
      .subscribe((res) => {
        this.scrollMovies.set(this.scrollMovies().concat(res));
        this.scMoviesPageEnd.set(this.scMoviesPageEnd() + 1);
        this.scroll({} as Event);
      });
  }
}
