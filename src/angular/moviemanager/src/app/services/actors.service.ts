import { Injectable } from '@angular/core';
import { Observable } from "rxjs/Observable";
import { Actor } from '../common/actor';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/of';

@Injectable()
export class ActorsService {
  private _reqOptionsArgs = { headers: new HttpHeaders().set( 'Content-Type', 'application/json' ) };
  
  constructor(private http: HttpClient) { }

  public findActorById(id: number) :Observable<Actor> {
      if(!id && id !== 0) {
          return Observable.of(null);
      }
      return this.http.get('/rest/actor/id/'+id, this._reqOptionsArgs).catch(error => {
          console.error( JSON.stringify( error ) );
          return Observable.throw( error );
          });
  }
  
  public findActorByName(name: string) :Observable<Actor[]> {
      if(!name) {
          return Observable.of([]);
      }
      return this.http.get('/rest/actor/'+name, this._reqOptionsArgs).catch(error => {
          console.error( JSON.stringify( error ) );
          return Observable.throw( error );
          });
  }
}
