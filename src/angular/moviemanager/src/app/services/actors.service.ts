import { Injectable } from '@angular/core';
import { Observable,of } from "rxjs";
import { Actor } from '../common/actor';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';

@Injectable()
export class ActorsService {
  private _reqOptionsArgs = { headers: new HttpHeaders().set( 'Content-Type', 'application/json' ) };
  
  constructor(private http: HttpClient) { }

  public findActorById(id: number) :Observable<Actor> {
      if(!id && id !== 0) {
          return of(null);
      }
      return this.http.get<Actor>('/rest/actor/id/'+id, this._reqOptionsArgs).pipe(catchError(error => {
          console.error( JSON.stringify( error ) );
          return Observable.throw( error );
          }));
  }
  
  public findActorByName(name: string) :Observable<Actor[]> {
      if(!name) {
          return of([]);
      }
      return this.http.get<Actor[]>('/rest/actor/'+name, this._reqOptionsArgs).pipe(catchError(error => {
          console.error( JSON.stringify( error ) );
          return Observable.throw( error );
          }));
  }
}
