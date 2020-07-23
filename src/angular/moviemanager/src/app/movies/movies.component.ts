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
