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
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { TokenService } from 'ngx-simple-charts/base-service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = 'app';
  
  constructor(private tokenService: TokenService, private router: Router) {}
  
  public loggedIn(): boolean {
	return !!this.tokenService.userId;
  }
  
  public logout(): void {
	this.router.navigate(['/actor/-1',{skipLocationChange: true}]).then(() => this.tokenService.logout());
  }
}
