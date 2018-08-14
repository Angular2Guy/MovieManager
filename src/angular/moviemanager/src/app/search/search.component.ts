import { Component, OnInit, OnDestroy, HostListener, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { Movie } from '../common/movie';
import { Actor } from '../common/actor';
import { Genere } from '../common/genere';
import { ActorsService } from '../services/actors.service';
import { MoviesService } from '../services/movies.service';
import { UsersService } from '../services/users.service';
import { Observable } from 'rxjs';
import { FormControl } from "@angular/forms";
import { map, tap, debounceTime, distinctUntilChanged, switchMap, flatMap } from 'rxjs/operators';


@Component( {
    selector: 'app-search',
    templateUrl: './search.component.html',
    styleUrls: ['./search.component.scss']
} )
export class SearchComponent implements OnInit, AfterViewInit {

    generes: Genere[];
    movieTitle = new FormControl();
    movies: Observable<Movie[]>;
    movieActor = new FormControl();
    actors: Observable<Actor[]>;
    importMovies: Movie[] = [];
    importMovieTitle = new FormControl();
    actorsLoading = false;
    moviesLoading = false;
    importMoviesLoading = false;
    showMenu = false;
    moviesByGenere: Movie[] = [];
    moviesByGenLoading = false;
    scrollMovies: Movie[] = [];
    scMoviesPageBegin = 1;
    scMoviesPageEnd = 1;
    scrollActors: Actor[] = [];
    scActorsPage = 1;
    @ViewChild( 'movies' ) moviesRef: ElementRef;
    private actorListOffset = 0;
    private scrollDone = true;

    constructor( private actorService: ActorsService, private movieService: MoviesService, private userService: UsersService ) { }

    ngOnInit() {
        this.actors = this.movieActor.valueChanges.pipe(
            debounceTime( 400 ),
            distinctUntilChanged(),
            tap(() => this.actorsLoading = true ),
            switchMap( name => this.actorService.findActorByName( name ) ),
            tap(() => this.actorsLoading = false ) );
        this.movies = this.movieTitle.valueChanges.pipe(
            debounceTime( 400 ),
            distinctUntilChanged(),
            tap(() => this.moviesLoading = true ),
            switchMap( title => this.movieService.findMovieByTitle( title ) ),
            tap(() => this.moviesLoading = false ) );
        this.userService.allGeneres().subscribe( res => this.generes = res );
    }

    ngAfterViewInit(): void {
        this.actorListOffset = this.moviesRef.nativeElement.getBoundingClientRect().y;
    }

    @HostListener( 'window:scroll' )
    scroll() {
        const ypos = window.pageYOffset + window.innerHeight;
        const contentHeight = this.moviesRef.nativeElement.offsetHeight + this.actorListOffset;
        if ( window.pageYOffset <= 1 && this.scrollDone ) {
            if ( this.scMoviesPageBegin >= 1 && this.scMoviesPageEnd > 1) {
                this.scrollDone = false;
                this.movieService.findMoviesByPage( this.scMoviesPageBegin ).subscribe( res => {
                    this.scMoviesPageBegin -= 1;
                    if ( this.scMoviesPageEnd - this.scMoviesPageBegin > 1 ) {
                        this.scMoviesPageEnd -= 1;
                        this.scrollMovies.splice( this.scrollMovies.length - 10, 10 );
                    }
                    this.scrollMovies = res.concat( this.scrollMovies );
                    setTimeout(() => {
                        window.scrollTo( 0, this.actorListOffset + contentHeight / 2);
                        console.log(this.scMoviesPageBegin);
                        console.log(this.scMoviesPageEnd);
                        this.scrollDone = true;
                    } );
                } );
            }
        }
        if ( ypos >= contentHeight && this.scrollDone ) {
            this.scrollDone = false;
            this.movieService.findMoviesByPage( this.scMoviesPageEnd ).subscribe( res => {
                //console.log( this.scMoviesPageEnd );
                if ( res.length > 0 ) {
                    this.scMoviesPageEnd += 1;
                    if ( this.scMoviesPageEnd > 1 ) {
                        this.scrollMovies.splice( 0, 10 );
                        this.scMoviesPageBegin += 1;
                    }
                    this.scrollMovies = this.scrollMovies.concat( res );
                }
                setTimeout(() => {
                    window.scrollTo( 0, this.actorListOffset + window.pageYOffset / 2);
                    console.log(this.scMoviesPageBegin);
                    console.log(this.scMoviesPageEnd);
                    this.scrollDone = true;
                } );
            } );
        }
    }

    loginClosed( closed: boolean ) {
        if ( closed ) {
            this.scrollDone = false;
            this.movieService.findMoviesByPage( this.scMoviesPageEnd ).pipe(
                    flatMap(res => {
                        this.scrollMovies = this.scrollMovies.concat(res);
                        this.scMoviesPageEnd += 1;                        
                        return this.movieService.findMoviesByPage(this.scMoviesPageEnd);
                    })
            ).subscribe( res => {                
                this.scrollMovies = this.scrollMovies.concat( res );
                this.scMoviesPageEnd += 1;
                setTimeout(() => {
                    window.scrollTo( 0, window.pageYOffset + 5 );
                    this.scrollDone = true;
                });                
            } );
        }
    }

    importMovie() {
        this.importMoviesLoading = true;
        let myTitle = this.importMovieTitle.value.replace( / /g, '+' );
        this.movieService.importMoveByTitle( myTitle ).subscribe( m => {
            this.importMovies = this.addNums( m );
            this.importMoviesLoading = false;
        } );
    }

    private addNums( movies: Movie[] ): Movie[] {
        for ( let i = 0; i < movies.length; i++ ) {
            movies[i].num = i;
        }
        return movies;
    }

    importSelMovie( movie: Movie ) {
        this.importMoviesLoading = true;
        this.importMovies = [];
        let myTitle = this.importMovieTitle.value.replace( / /g, '+' );
        this.movieService.importMoveByTitleAndId( myTitle, movie.num ).subscribe( imported => {
            if ( imported )
                this.importMoviesLoading = false;
        } );
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
}
