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
import { ActivatedRoute, Router } from '@angular/router';
import { QueryParam } from '../model/common';
import { Movie } from '../model/movie';
import { MoviesService } from '../services/movies.service';

enum ImportState {Idle='idle' ,MoviesLoading='moviesLoading', Importing='importing',
   ImportSuccess='success', ImportFailed='failed'}

@Component({
  selector: 'app-movie-import',
  templateUrl: './movie-import.component.html',
  styleUrls: ['./movie-import.component.scss']
})
export class MovieImportComponent implements OnInit {
  public ImportState = ImportState;
  public importState = ImportState.Idle;
  public importMovies: Movie[] = [];
  
  constructor(private moviesService: MoviesService, private router: Router, private activeRoute: ActivatedRoute) { }

  ngOnInit(): void {
	this.activeRoute.queryParamMap.subscribe(queryParamMap => {
		if(!!queryParamMap.get(QueryParam.MovieName)) {
		   this.importState = ImportState.MoviesLoading;
		   this.loadMatchingMovies(decodeURIComponent(queryParamMap.get(QueryParam.MovieName)));
		} else {
			this.router.navigate(['search']);
		}
	});	
  }

  public back(): void {
	this.router.navigate(['/search']);
  }

  private loadMatchingMovies(movieTitle: string) {
	this.moviesService.importMovieByTitle( movieTitle ).subscribe( m => {
       this.importMovies = this.addNums( m );
       this.importState = ImportState.Idle;
    } );
  }

   importSelMovie( movie: Movie ) {
      this.importState = ImportState.Importing;
      this.importMovies = [];
      this.moviesService.importMovieByMovieDbId( movie.movie_id ).subscribe( imported => {            
         this.importState = imported ? ImportState.ImportSuccess : ImportState.ImportFailed;
         const timeoutMs = imported ? 3000 : 6000;
         setTimeout(() => this.router.navigate(['search']), timeoutMs);
      } );
    }

    private addNums( movies: Movie[] ): Movie[] {
        for ( let i = 0; i < movies.length; i++ ) {
            movies[i].num = i;
        }
        return movies;
    }
}
