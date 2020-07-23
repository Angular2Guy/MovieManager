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
import { Component, OnInit, EventEmitter, Output } from '@angular/core';
import { UsersService } from '../services/users.service';
import { FormControl } from "@angular/forms";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
    
    @Output() loginClosed = new EventEmitter<boolean>();
    showModal = true;
    loginName = new FormControl();
    password = new FormControl();  
    movieDbKey = new FormControl();
    modalMsg = '';
    
  constructor(private userService: UsersService) { }

  ngOnInit() {
      this.showModal = !this.userService.loggedIn;
  }

  loginUser() {        
      this.userService.login(this.loginName.value, this.password.value).subscribe((res: boolean) => {          
          this.showModal = !res;
          this.userService.loggedIn = res;
          this.modalMsg = res ? '' : 'Login Failed';  
          this.loginClosed.emit(res);
      });
  }
  
  cancelUser() {
      this.loginName.setValue('');
      this.password.setValue('');
      this.movieDbKey.setValue('');
      this.modalMsg = '';
  }
  
  signinUser() {
      this.userService.signin(this.loginName.value, this.password.value, this.movieDbKey.value).subscribe((res: boolean) =>{
          this.showModal = !res;
          this.modalMsg = res ? '' : 'Signin Failed';
          this.loginClosed.emit(res);
      });
  }
  
}
