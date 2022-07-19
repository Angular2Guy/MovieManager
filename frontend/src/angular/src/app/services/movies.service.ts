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
import { Injectable } from '@angular/core';
import { Observable,of, throwError } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';
import { Movie } from '../common/movie';
import { Genere } from '../common/genere';

@Injectable({
 providedIn: 'root',
})
export class MoviesService {
    private reqOptionsArgs = { headers: new HttpHeaders().set( 'Content-Type', 'application/json' ) };

    constructor(private http: HttpClient) { }

    public findMoviesByPage(page: number): Observable<Movie[]> {
        return this.http.get<Movie[]>('/rest/movie/pages?page='+page, this.reqOptionsArgs).pipe(catchError(error => {
            console.error( JSON.stringify( error ) );
            return throwError( error);
        }));
    }

    public findMoviesByGenereId(id: number): Observable<Movie[]> {
        return this.http.get<Movie[]>('/rest/movie/genere/id/'+id, this.reqOptionsArgs).pipe(catchError(error => {
            console.error( JSON.stringify( error ) );
            return throwError( error);
        }));
    }

    public findMovieById(id: number): Observable<Movie> {
        if(!id && id !== 0) {
            return of(null);
        }
        return this.http.get<Movie>('/rest/movie/id/'+id, this.reqOptionsArgs).pipe(catchError(error => {
            console.error( JSON.stringify( error ) );
            return throwError( error);
            }));
    }

    public deleteMovieById(id: number): Observable<boolean> {
        if(!id && id !== 0) {
            return of(false);
        }
        return this.http.delete<boolean>('/rest/movie/id/'+id, this.reqOptionsArgs).pipe(catchError(error => {
            console.error( JSON.stringify( error ) );
            return throwError( error);
        }));
    }

    public findMovieByTitle(title: string): Observable<Movie[]> {
        if(!title) {
            return of([]);
        }
        return this.http.get<Movie[]>('/rest/movie/'+title, this.reqOptionsArgs).pipe(catchError(error => {
            console.error( JSON.stringify( error ) );
            return throwError( error);
            }));
    }

    public importMovieByTitle(title: string): Observable<Movie[]> {
        if(!title) {
            return of([]);
        }
        return this.http.get<Movie[]>('/rest/movie/import/'+title, this.reqOptionsArgs).pipe(catchError(error => {
            console.error( JSON.stringify( error ) );
            return throwError( error);
            }));
    }

    public importMovieByMovieDbId(movieDbId: number): Observable<boolean> {
        if(!movieDbId) {
            console.log('movieDbId: '+movieDbId);
            return of(false);
        }
        return this.http.get<boolean>('/rest/movie/import/movieid/'+movieDbId, this.reqOptionsArgs).pipe(catchError(error => {
            console.error( JSON.stringify( error ) );
            return throwError( error);
            }));
    }
  	public allGeneres(): Observable<Genere[]> {
      return this.http.get<Genere[]>('/rest/movie/generes', this.reqOptionsArgs).pipe(catchError(error => {
          console.error( JSON.stringify( error ) );
          return throwError( error );
      }));
  	}
}
