import { Injectable } from '@angular/core';
import { Observable,of } from "rxjs";
import { Actor } from '../common/actor';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';
import { Movie } from '../common/movie';
import { Genere } from '../common/genere';

@Injectable()
export class MoviesService {
    private _reqOptionsArgs = { headers: new HttpHeaders().set( 'Content-Type', 'application/json' ) };
    
    constructor(private http: HttpClient) { }
    
    public findMoviesByGenereId(id: number) : Observable<Movie[]> {
        return this.http.get<Movie[]>('/rest/movie/genere/id/'+id, this._reqOptionsArgs).pipe(catchError(error => {
            console.error( JSON.stringify( error ) );
            return Observable.throw( error );
        }));
    }
    
    public findMovieById(id: number) :Observable<Movie> {
        if(!id && id !== 0) {
            return of(null);
        }
        return this.http.get<Movie>('/rest/movie/id/'+id, this._reqOptionsArgs).pipe(catchError(error => {
            console.error( JSON.stringify( error ) );
            return Observable.throw( error );
            }));
    }

    public deleteMovieById(id: number) :Observable<boolean> {
        if(!id && id !== 0) {
            return of(false);
        }
        return this.http.delete<boolean>('/rest/movie/id/'+id, this._reqOptionsArgs).pipe(catchError(error => {
            console.error( JSON.stringify( error ) );
            return Observable.throw( error );
        }));
    }
    
    public findMovieByTitle(title: string) :Observable<Movie[]> {
        if(!title) {
            return of([]);
        }
        return this.http.get<Movie[]>('/rest/movie/'+title, this._reqOptionsArgs).pipe(catchError(error => {
            console.error( JSON.stringify( error ) );
            return Observable.throw( error );
            }));
    }
    
    public importMoveByTitle(title: string) :Observable<Movie[]> {
        if(!title) {
            return of([]);
        }
        return this.http.get<Movie[]>('/rest/movie/import/'+title, this._reqOptionsArgs).pipe(catchError(error => {
            console.error( JSON.stringify( error ) );
            return Observable.throw( error );
            }));
    }
    
    public importMoveByTitleAndId(title: string, id: number) :Observable<boolean> {
        if(!title) {
            console.log("title: "+title+" id: "+id);
            return of(false);
        }
        return this.http.get<boolean>('/rest/movie/import/'+title+'/number/'+id, this._reqOptionsArgs).pipe(catchError(error => {
            console.error( JSON.stringify( error ) );
            return Observable.throw( error );
            }));
    }
}
