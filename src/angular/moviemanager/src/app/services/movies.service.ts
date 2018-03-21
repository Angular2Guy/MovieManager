import { Injectable } from '@angular/core';
import { Observable } from "rxjs/Observable";
import { Actor } from '../common/actor';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/of';
import { Movie } from '../common/movie';
import { Genere } from '../common/genere';

@Injectable()
export class MoviesService {
    private _reqOptionsArgs = { headers: new HttpHeaders().set( 'Content-Type', 'application/json' ) };
    
    constructor(private http: HttpClient) { }
    
    public findMoviesByGenereId(id: number) : Observable<Movie[]> {
        return this.http.get('/rest/movie/genere/id/'+id, this._reqOptionsArgs).catch(error => {
            console.error( JSON.stringify( error ) );
            return Observable.throw( error );
        });
    }
    
    public findMovieById(id: number) :Observable<Movie> {
        if(!id && id !== 0) {
            return Observable.of(null);
        }
        return this.http.get('/rest/movie/id/'+id, this._reqOptionsArgs).catch(error => {
            console.error( JSON.stringify( error ) );
            return Observable.throw( error );
            });
    }

    public deleteMovieById(id: number) :Observable<boolean> {
        if(!id && id !== 0) {
            return Observable.of(false);
        }
        return this.http.delete('/rest/movie/id/'+id, this._reqOptionsArgs).catch(error => {
            console.error( JSON.stringify( error ) );
            return Observable.throw( error );
        });
    }
    
    public findMovieByTitle(title: string) :Observable<Movie[]> {
        if(!title) {
            return Observable.of([]);
        }
        return this.http.get('/rest/movie/'+title, this._reqOptionsArgs).catch(error => {
            console.error( JSON.stringify( error ) );
            return Observable.throw( error );
            });
    }
    
    public importMoveByTitle(title: string) :Observable<Movie[]> {
        if(!title) {
            return Observable.of([]);
        }
        return this.http.get('/rest/movie/import/'+title, this._reqOptionsArgs).catch(error => {
            console.error( JSON.stringify( error ) );
            return Observable.throw( error );
            });
    }
    
    public importMoveByTitleAndId(title: string, id: number) :Observable<boolean> {
        if(!title) {
            console.log("title: "+title+" id: "+id);
            return Observable.of(false);
        }
        return this.http.get('/rest/movie/import/'+title+'/number/'+id, this._reqOptionsArgs).catch(error => {
            console.error( JSON.stringify( error ) );
            return Observable.throw( error );
            });
    }
}
