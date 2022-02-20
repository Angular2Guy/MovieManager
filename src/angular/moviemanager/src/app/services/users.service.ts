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
import { Observable, of } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { User } from '../common/user';

@Injectable({
 providedIn: 'root',
})
export class UsersService {
  public loggedIn = false;
  private reqOptionsArgs = { headers: new HttpHeaders().set( 'Content-Type', 'application/json' ) };

  constructor(private http: HttpClient) { }

  public login(login: string, password: string): Observable<boolean> {
      if(!login || !password) {
          return of(false);
      }
      const u = new User();
      u.username = login;
      u.password = password;
      return this.http.post<boolean>('/rest/user/login', u, this.reqOptionsArgs).pipe(catchError(error => {
          console.error( JSON.stringify( error ) );
          return of(false);
          }));
  }

  public signin(login: string, password: string, movieDbKey: string): Observable<boolean> {
      if(!login || !password || !movieDbKey) {
          return of(false);
      }
      const u = new User();
      u.username = login;
      u.password = password;
      u.moviedbkey = movieDbKey;
      return this.http.post<boolean>('/rest/user/signin', u, this.reqOptionsArgs).pipe(catchError(error => {
          console.error( JSON.stringify( error ) );
          return of(false);
          }));
  }
}
