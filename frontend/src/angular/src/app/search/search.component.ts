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
import { Component, OnInit, HostListener, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { Movie } from '../model/movie';
import { Actor } from '../model/actor';
import { Genere } from '../model/genere';
import { ActorsService } from '../services/actors.service';
import { MoviesService } from '../services/movies.service';
import { UsersService } from '../services/users.service';
import { iif, of, Observable } from 'rxjs';
import { FormControl } from '@angular/forms';
import { tap, debounceTime, distinctUntilChanged, switchMap, catchError } from 'rxjs/operators';
import { ActivatedRoute, Router } from '@angular/router';
import { QueryParam } from '../model/common';


@Component( {
    selector: 'app-search',
    templateUrl: './search.component.html',
    styleUrls: ['./search.component.scss']
} )
export class SearchComponent implements OnInit, AfterViewInit {
    @ViewChild('movies') moviesRef: ElementRef;
    generes: Genere[];
    movieTitle = new FormControl('');
    movies: Observable<Movie[]>;
    movieActor = new FormControl('');
    actors: Observable<Actor[]>;
    importMovies: Movie[] = [];
    importMovieTitle = new FormControl('');
    actorsLoading = false;
    moviesLoading = false;
    importMoviesLoading = false;
    showMenu = false;
    moviesByGenere: Movie[] = [];
    moviesByGenLoading = false;
    scrollMovies: Movie[] = [];
    scMoviesPageEnd = 1;
    loading = false;
    allMoviesLoaded = false;
    private actorListOffset = 0;

    constructor( private actorService: ActorsService,
            private movieService: MoviesService,
            private userService: UsersService,
            private route: ActivatedRoute,
            private router: Router) { }

	@HostListener( 'window:scroll' ,['$event'])
    scroll($event: any) {
        const ypos = window.pageYOffset + window.innerHeight;
        const contentHeight = !this.moviesRef ? 100000000 : this.moviesRef.nativeElement.offsetHeight + this.actorListOffset;
        if(ypos >= contentHeight) {
            this.fetchMore();
        }
    }

	importMovie() {
        const myTitle = encodeURIComponent(this.importMovieTitle.value);
        this.router.navigate(['movie-import'],{ 'queryParams': { [QueryParam.MovieName]: myTitle }});
    }

    dropDown() {
        this.showMenu = !this.showMenu;
        if ( this.moviesByGenere.length > 0 ) {
            this.showMenu = false;
        }
        this.moviesByGenere = [];
    }

    showGenere( id: number ) {
        this.showMenu = false;
        this.moviesByGenLoading = true;
        this.movieService.findMoviesByGenereId( id ).subscribe( res => {
            this.moviesByGenere = res;
            this.moviesByGenLoading = false;
        } );
    }

    showFilterMovies(): void {
	   this.router.navigate(['filter-movies']);
    }

    movieDetails(movie: Movie) {
        this.router.navigateByUrl('movie/'+movie.id);
    }

    ngOnInit() {	
        this.actors = this.movieActor.valueChanges.pipe(
            debounceTime( 400 ),
            distinctUntilChanged(),
            tap(() => this.actorsLoading = true ),
            switchMap((name: string) => iif(() => name.length > 2,
				this.actorService.findActorByName( name).pipe(catchError(error => this.handleRxJsProblem(error))), of([]))),
            tap(() => this.actorsLoading = false ) );
        this.movies = this.movieTitle.valueChanges.pipe(
            debounceTime( 400 ),
            distinctUntilChanged(),
            tap(() => this.moviesLoading = true ),
			switchMap((title: string) => iif(() => title.length > 2,
				this.movieService.findMovieByTitle( title ).pipe(catchError(error => this.handleRxJsProblem(error))), of([]))),
            tap(() => this.moviesLoading = false ) );
        if(this.userService.loggedIn) {
            this.movieService.allGeneres().subscribe( res => this.generes = res );
        }
        this.route.url.subscribe(() => {
            if(this.userService.loggedIn) {
                this.initScrollMovies();
            }
        });
    }

   private handleRxJsProblem(error: unknown): Observable<any[]> {
	  console.log(error);
	  this.router.navigate(['/']);
	  return of([]);
   }

    ngAfterViewInit(): void {
        this.actorListOffset = this.moviesRef.nativeElement.getBoundingClientRect().y;
    }

    fetchMore() {
        if (this.allMoviesLoaded || this.loading) {return;}
        this.loading = true;
        this.movieService.findMoviesByPage( this.scMoviesPageEnd )
           .pipe(catchError(error => this.handleRxJsProblem(error))).subscribe( res => {
            if(res.length > 0) {
                this.scrollMovies = this.scrollMovies.concat( res );
                this.scMoviesPageEnd += 1;
            } else {
                this.allMoviesLoaded = true;
            }
            this.loading = false;
        } );
    }

    loginClosed( closed: boolean ) {
        if ( this.userService.loggedIn && closed ) {
			this.movieService.allGeneres().subscribe( res => this.generes = res );
            this.initScrollMovies();
        }
    }

    private initScrollMovies() {
        this.loading = false;
        this.allMoviesLoaded = false;
        this.movieService.findMoviesByPage( this.scMoviesPageEnd ).subscribe( res => {
            this.scrollMovies = this.scrollMovies.concat( res );
            this.scMoviesPageEnd += 1;
        } );
    }
}
