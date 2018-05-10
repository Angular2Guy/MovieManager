import { Injectable } from '@angular/core';
import { Observable,of } from "rxjs";
import { Actor } from '../common/actor';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';
import { User } from '../common/user';
import { Genere } from '../common/genere';

@Injectable()
export class UsersService {
  private _reqOptionsArgs = { headers: new HttpHeaders().set( 'Content-Type', 'application/json' ) };
  public loggedIn = false;
    
  constructor(private http: HttpClient) { }

  public login(login: string, password: string) :Observable<boolean> {
      if(!login || !password) {
          return of(false);
      }
      let u = new User();
      u.username = login;
      u.password = password;
      return this.http.post<boolean>('/rest/user/login', u, this._reqOptionsArgs).pipe(catchError(error => {
          console.error( JSON.stringify( error ) );
          return of(false);         
          }));
  }
  
  public signin(login: string, password: string, movieDbKey: string) :Observable<boolean> {
      if(!login || !password || !movieDbKey) {
          return of(false);
      }
      let u = new User();
      u.username = login;
      u.password = password;
      u.moviedbkey = movieDbKey;
      return this.http.post<boolean>('/rest/user/signin', u, this._reqOptionsArgs).pipe(catchError(error => {
          console.error( JSON.stringify( error ) );
          return of(false);          
          }));
  }
  
  public allGeneres() :Observable<Genere[]> {
      return this.http.get<Genere[]>('/rest/user/genere', this._reqOptionsArgs).pipe(catchError(error => {
          console.error( JSON.stringify( error ) );
          return Observable.throw( error );
      }));
  }
}
