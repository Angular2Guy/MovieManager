import { Component, OnInit } from '@angular/core';
import { MoviesService } from '../services/movies.service';
import {ActivatedRoute, Router, ParamMap } from '@angular/router';
import { Movie } from '../common/movie';

@Component({
  selector: 'app-movies',
  templateUrl: './movies.component.html',
  styleUrls: ['./movies.component.scss']
})
export class MoviesComponent implements OnInit {

  movie: Movie = null;
  delMovie = false;
  
  constructor(private route: ActivatedRoute, private router: Router, private movieService: MoviesService) { }

  ngOnInit() {
      this.movieService.findMovieById(Number(this.route.snapshot.paramMap.get('id')))
          .subscribe(movie => this.movie = movie);      
  }

  deleteMovie() {
      console.log("delete movie id: "+this.movie.id+" title: "+this.movie.title);
      this.delMovie = true;
      this.movieService.deleteMovieById(this.movie.id).subscribe(result => {
          this.delMovie = false;
          if(!result) {
              console.log("Delete of movie id: "+this.movie.id+" failed.");
          } else {
              this.router.navigateByUrl("/search");
          }
      });
  }
}
