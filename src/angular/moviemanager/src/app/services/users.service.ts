import { Injectable } from '@angular/core';
import { Observable } from "rxjs/Observable";
import { Actor } from '../common/actor';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import 'rxjs/add/operator/catch';
import 'rxjs/add/observable/of';
import 'rxjs/add/observable/throw';
import { User } from '../common/user';
import { Genere } from '../common/genere';

@Injectable()
export class UsersService {
  private _reqOptionsArgs = { headers: new HttpHeaders().set( 'Content-Type', 'application/json' ) };
  public loggedIn = false;
    
  constructor(private http: HttpClient) { }

  public login(login: string, password: string) :Observable<boolean | any> {
      if(!login || !password) {
          return Observable.of(false);
      }
      let u = new User();
      u.username = login;
      u.password = password;
      return this.http.post('/rest/user/login', u, this._reqOptionsArgs).catch(error => {
          console.error( JSON.stringify( error ) );
          return Observable.of(false);         
          });
  }
  
  public signin(login: string, password: string, movieDbKey: string) :Observable<boolean | any> {
      if(!login || !password || !movieDbKey) {
          return Observable.of(false);
      }
      let u = new User();
      u.username = login;
      u.password = password;
      u.moviedbkey = movieDbKey;
      return this.http.post('/rest/user/signin', u, this._reqOptionsArgs).catch(error => {
          console.error( JSON.stringify( error ) );
          return Observable.of(false);          
          });
  }
  
  public allGeneres() :Observable<Genere[]> {
      return this.http.get('/rest/user/genere', this._reqOptionsArgs).catch(error => {
          console.error( JSON.stringify( error ) );
          return Observable.throw( error );
      });
  }
}
