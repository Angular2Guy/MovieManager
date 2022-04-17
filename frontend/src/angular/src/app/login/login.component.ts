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
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';

enum ControlName {
	loginName = 'loginName',
	password = 'password',
	movieDbKey = 'movieDbKey',	
	emailAddress = 'emailAddress'
}

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

    @Output() loginClosed = new EventEmitter<boolean>();
    ControlName = ControlName;
    showModal = true;
    loginFormGroup: FormGroup;
    modalMsg = '';
    tillNextLogin = 0;

  constructor(private userService: UsersService, private formBuilder: FormBuilder) { 
	this.loginFormGroup = formBuilder.group({
		[ControlName.loginName]: '',
		[ControlName.password]: '',
		[ControlName.movieDbKey]: '',
		[ControlName.emailAddress]: ''
	});
  }

  ngOnInit() {
      this.showModal = !this.userService.loggedIn;
  }

  loginUser() {
      this.userService.login(this.loginFormGroup.controls[ControlName.loginName].value, 
      	this.loginFormGroup.controls[ControlName.password].value).subscribe((myTillNextLogin: number) => {
	      const res = myTillNextLogin <= 0;
	      this.tillNextLogin = myTillNextLogin;
          this.showModal = !res;
          this.userService.loggedIn = res;
          this.modalMsg = res ? '' : $localize `:@@loginErrorMsg:Login Failed. Try again in: ${myTillNextLogin} seconds.`;
          this.loginClosed.emit(res);
      });
  }

  cancelUser() {
      this.loginFormGroup.controls[ControlName.loginName].setValue('');
      this.loginFormGroup.controls[ControlName.password].setValue('');
      this.loginFormGroup.controls[ControlName.movieDbKey].setValue('');
      this.loginFormGroup.controls[ControlName.emailAddress].setValue('');
      this.modalMsg = '';
  }

  signinUser() {
      this.userService.signin(this.loginFormGroup.controls[ControlName.loginName].value, 
        this.loginFormGroup.controls[ControlName.password].value, 
        this.loginFormGroup.controls[ControlName.movieDbKey].value).subscribe((res: boolean) =>{
          this.showModal = !res;
          this.modalMsg = res ? '' : 'Signin Failed';
          this.loginClosed.emit(res);
      });
  }

}
