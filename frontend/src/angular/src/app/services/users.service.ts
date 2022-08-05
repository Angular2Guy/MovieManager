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
import { Observable, of, throwError } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';
import { User } from '../model/user';
import { TokenService } from './token.service';
import { Router } from '@angular/router';

@Injectable({
 providedIn: 'root',
})
export class UsersService {
  public loggedIn = false;

  constructor(private http: HttpClient, private tokenService: TokenService, private router: Router) { }

  public login(login: string, password: string): Observable<number> {
      if(!login || !password) {
          return throwError(() => 'login and password needed.');
      }
      const u = new User();
      u.username = login;
      u.password = password;
      return this.http.post<User>('/rest/auth/login', u).pipe(map(myUser => {				
			if(!!myUser?.id && !!myUser?.token && myUser?.secUntilNexLogin <= 0) {
				this.tokenService.token = myUser.token;
				this.tokenService.userId = myUser.id;
				this.loggedIn = true;				
				return myUser.secUntilNexLogin;
			}
			this.loggedIn = false;
			return !!myUser?.secUntilNexLogin ? myUser?.secUntilNexLogin : 24 * 60 * 60;
			;
		}));
  }

  public signin(login: string, password: string, movieDbKey: string): Observable<boolean> {
	this.loggedIn = false
      if(!login || !password || !movieDbKey) {
          return of(false);
      }
      const u = new User();
      u.username = login;
      u.password = password;
      u.moviedbkey = movieDbKey;
      return this.http.post<boolean>('/rest/auth/signin', u).pipe(catchError(error => {
        console.error( JSON.stringify( error ) );
        return of(false);
      }));
  }
  
  public logout(): Observable<boolean> {
	this.loggedIn = false;
	return this.http.put<boolean>('/rest/auth/logout',{}).pipe(tap(() => this.tokenService.clear()), 
	  tap(() => this.router.navigate(['/actor/-1',{skipLocationChange: true}]).then(() => this.router.navigate(['/']))));
  }
}
