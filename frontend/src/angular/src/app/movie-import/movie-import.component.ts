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
import { ActivatedRoute, Router, RouterModule } from "@angular/router";
import { QueryParam } from "../model/common";
import { Movie } from "../model/movie";
import { MoviesService } from "../services/movies.service";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { CommonModule } from "@angular/common";

enum ImportState {
  Idle = "idle",
  MoviesLoading = "moviesLoading",
  Importing = "importing",
  ImportSuccess = "success",
  ImportFailed = "failed",
}

@Component({
  selector: "app-movie-import",
  imports: [CommonModule, RouterModule],
  templateUrl: "./movie-import.component.html",
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ["./movie-import.component.scss"],
})
export class MovieImportComponent implements OnInit {
  protected ImportState = ImportState;
  protected importState = signal(ImportState.Idle);
  protected importMovies = signal<Movie[]>([]);
  private readonly destroy: DestroyRef = inject(DestroyRef);

  constructor(
    private moviesService: MoviesService,
    private router: Router,
    private activeRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activeRoute.queryParamMap.subscribe((queryParamMap) => {
      if (!!queryParamMap.get(QueryParam.MovieName)) {
        this.importState.set(ImportState.MoviesLoading);
        this.loadMatchingMovies(
          decodeURIComponent(queryParamMap.get(QueryParam.MovieName) ?? ''),
        );
      } else {
        this.router.navigate(["search"]);
      }
    });
  }

  public back(): void {
    this.router.navigate(["/search"]);
  }

  private loadMatchingMovies(movieTitle: string) {
    this.moviesService
      .importMovieByTitle(movieTitle)
      .pipe(takeUntilDestroyed(this.destroy))
      .subscribe((m) => {
        this.importMovies.set(this.addNums(m));
        this.importState.set(ImportState.Idle);
      });
  }

  importSelMovie(movie: Movie) {
    this.importState.set(ImportState.Importing);
    this.importMovies.set([]);
    this.moviesService
      .importMovieByMovieDbId(movie.movie_id)
      .pipe(takeUntilDestroyed(this.destroy))
      .subscribe((imported) => {
        this.importState.set(imported
          ? ImportState.ImportSuccess
          : ImportState.ImportFailed);
        const timeoutMs = imported ? 3000 : 6000;
        setTimeout(() => this.router.navigate(["search"]), timeoutMs);
      });
  }

  private addNums(movies: Movie[]): Movie[] {
    for (let i = 0; i < movies.length; i++) {
      movies[i].num = i;
    }
    return movies;
  }
}
