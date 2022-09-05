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
import { HttpHeaders, HttpClient } from '@angular/common/http';
import { Observable, timer, Subscription, Subject } from 'rxjs';
import { shareReplay, switchMap, map, takeUntil, retry } from 'rxjs/operators';

export interface RefreshTokenResponse {
	refreshToken: string;
}

@Injectable({
  providedIn: 'root'
})
export class TokenService {
  private myTokenCache: Observable<RefreshTokenResponse>; 	
  private readonly CACHE_SIZE = 1;
  private readonly REFRESH_INTERVAL = 45000; //45 sec
  private myToken: string;
  private myUserId: number;
  private myTokenSubscription: Subscription;
  private stopTimer: Subject<boolean>;

  constructor(private http: HttpClient) { }

  private refreshToken(): Observable<RefreshTokenResponse> {	
	return this.http.get<RefreshTokenResponse>('/rest/auth/refreshToken', {
		headers: this.createTokenHeader()
	});
  }

	public createTokenHeader(): HttpHeaders {
	    let reqOptions = new HttpHeaders().set( 'Content-Type', 'application/json' )
	    if(this.token) {
	        reqOptions = new HttpHeaders().set( 'Content-Type', 'application/json' ).set('Authorization', `Bearer ${this.token}`);
	    }
	    return reqOptions;
	}
	
  public clear() {
	if(this.myTokenSubscription) {
		this.myTokenSubscription.unsubscribe();		
	}
	if(this.stopTimer) {		
		this.stopTimer.next(true);
		this.stopTimer = null;
	}
	this.myTokenCache = null;
	this.myToken = null;
	this.myUserId = null;
	// this.router.navigate(['/login']);
  }

  get tokenStream(): Observable<string> {
	return this.myTokenCache.pipe(map(response => response.refreshToken));
  }

  get token(): string {	
	return this.myToken;
  }
 
  set token(token: string) {
	this.myToken = token;
	if(token && !this.myTokenCache) {
		const myStopTimer = new Subject<boolean>();
		this.stopTimer = myStopTimer;
		const myTimer = timer(0, this.REFRESH_INTERVAL);
		this.myTokenCache = myTimer.pipe(
			takeUntil(myStopTimer),
			switchMap(() => this.refreshToken()),
			retry({count: 3, delay: 2000, resetOnSuccess: true}),
			shareReplay(this.CACHE_SIZE));
		this.myTokenSubscription = this.myTokenCache
		   .subscribe({
              next: newToken => this.myToken = newToken.refreshToken, 
              error: () => this.clear()
           });
	}
  }

  get userId(): number {
	return this.myUserId;
  }

  set userId(userId: number) {
	this.myUserId = userId;
  }
}
