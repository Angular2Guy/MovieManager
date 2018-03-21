import { Component, OnInit } from '@angular/core';
import { Movie } from '../common/movie';
import { Actor } from '../common/actor';
import { Genere } from '../common/genere';
import { ActorsService } from '../services/actors.service';
import { MoviesService } from '../services/movies.service';
import { UsersService } from '../services/users.service';
import { Observable } from 'rxjs/Observable';
import { FormControl } from "@angular/forms";
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/operator/switchMap';
import 'rxjs/add/operator/do';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.scss']
})
export class SearchComponent implements OnInit {
      
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
  
  constructor(private actorService: ActorsService, private movieService: MoviesService, private userService: UsersService) { }

  ngOnInit() {      
      this.actors = this.movieActor.valueChanges
        .debounceTime(400)
        .distinctUntilChanged()
        .do(() => this.actorsLoading = true)
        .switchMap(name => this.actorService.findActorByName(name))
        .do(() => this.actorsLoading = false);
      this.movies = this.movieTitle.valueChanges
        .debounceTime(400)
        .distinctUntilChanged()
        .do(() => this.moviesLoading = true)
        .switchMap(title => this.movieService.findMovieByTitle(title))
        .do(() => this.moviesLoading = false);
        this.userService.allGeneres().subscribe(res => this.generes = res);
  }
  
  importMovie() {
      this.importMoviesLoading = true;
      let myTitle = this.importMovieTitle.value.replace(/ /g,'+');      
      this.movieService.importMoveByTitle(myTitle).subscribe(m => {
          this.importMovies = this.addNums(m);
          this.importMoviesLoading = false;
      });      
  }
  
  private addNums(movies: Movie[]) : Movie[] {
      for(let i = 0;i<movies.length;i++) {
          movies[i].num = i;
      }
      return movies;
  }
  
  importSelMovie(movie: Movie) {
      this.importMoviesLoading = true;
      this.importMovies = [];
      let myTitle = this.importMovieTitle.value.replace(/ /g,'+');
      this.movieService.importMoveByTitleAndId(myTitle, movie.num).subscribe(imported => {
          if(imported) 
              this.importMoviesLoading = false;
          });
  }
  
  dropDown() {
      this.showMenu = !this.showMenu;
      if(this.moviesByGenere.length > 0){
          this.showMenu = false;
      }
      this.moviesByGenere = [];
  }
  
  showGenere(id: number) {
      this.showMenu = false;      
      this.moviesByGenLoading = true;      
      this.movieService.findMoviesByGenereId(id).subscribe(res => {
          this.moviesByGenere = res;
          this.moviesByGenLoading = false;
      });
  }
}
