import { Component, OnInit, OnDestroy, HostListener, ViewChild, ElementRef, AfterViewInit, OnChanges, SimpleChanges } from '@angular/core';
import { Movie } from '../common/movie';
import { Actor } from '../common/actor';
import { Genere } from '../common/genere';
import { ActorsService } from '../services/actors.service';
import { MoviesService } from '../services/movies.service';
import { UsersService } from '../services/users.service';
import { Observable } from 'rxjs';
import { FormControl } from "@angular/forms";
import { map, tap, debounceTime, distinctUntilChanged, switchMap, flatMap } from 'rxjs/operators';
import { ActivatedRoute, Router } from "@angular/router";
import { ChangeEvent, VirtualScrollerComponent } from "ngx-virtual-scroller/dist/virtual-scroller";


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
    scMoviesPageEnd = 1;
    @ViewChild( 'movies' ) moviesRef: ElementRef;
    loading = false;
    allMoviesLoaded = false;
    @ViewChild(VirtualScrollerComponent)
    private virtualScroller: VirtualScrollerComponent;
    private actorListOffset = 0;
    

    constructor( private actorService: ActorsService, 
            private movieService: MoviesService, 
            private userService: UsersService,
            private route: ActivatedRoute,
            private router: Router) { }

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
        this.route.url.subscribe(res => {
            if(this.userService.loggedIn) {
                this.initScrollMovies();
            }
        });
    }

    ngAfterViewInit(): void {
        this.actorListOffset = this.moviesRef.nativeElement.getBoundingClientRect().y;
    }

    @HostListener( 'window:scroll' )
    scroll() {
        const ypos = window.pageYOffset + window.innerHeight;
        const contentHeight = this.moviesRef.nativeElement.offsetHeight + this.actorListOffset;        
        if(ypos >= contentHeight) {
            this.fetchMore();
        }
    }
    
    fetchMore() {
        if (this.allMoviesLoaded || this.loading) return;
        this.loading = true;
        this.movieService.findMoviesByPage( this.scMoviesPageEnd ).subscribe( res => {
            if(res.length > 0) {
                this.scrollMovies = this.scrollMovies.concat( res );
                this.scMoviesPageEnd += 1;
                this.virtualScroller.refresh();            
            } else {
                this.allMoviesLoaded = true;
            }
            this.loading = false;
        } );
    }

    loginClosed( closed: boolean ) {
        if ( closed ) {
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
    
    movieDetails(movie: Movie) {
        this.router.navigateByUrl('movie/'+movie.id);
    }
}
