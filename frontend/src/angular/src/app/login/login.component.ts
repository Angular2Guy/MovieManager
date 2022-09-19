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
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TokenService } from 'ngx-simple-charts/base-service';

enum ControlName {
	LoginName = 'loginName',
	Password = 'password',
	MovieDbKey = 'movieDbKey',	
	EmailAddress = 'emailAddress'
}

enum MessageType {
	Info = 'info',
	Error = 'error'
}

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

    @Output() loginClosed = new EventEmitter<boolean>();
    ControlName = ControlName;
    MessageType = MessageType;
    showModal = true;
    loginFormGroup: FormGroup;
    modalMsg = '';
    modalMsgType = MessageType.Error; 
    tillNextLogin = 0;

  constructor(private userService: UsersService, formBuilder: FormBuilder, private tokenService: TokenService) { 
	this.loginFormGroup = formBuilder.group({
		[ControlName.LoginName]: ['', [Validators.required, Validators.minLength(2)]],
		[ControlName.Password]: ['', [Validators.required, Validators.minLength(2)]],
		[ControlName.MovieDbKey]: '',
		[ControlName.EmailAddress]: ''
	});	
  }

  ngOnInit() {
      this.showModal = !this.tokenService.userId;   
      this.loginFormGroup.markAllAsTouched();
    //  console.log(this.loginFormGroup.invalid || this.loginFormGroup.controls[ControlName.loginName].untouched || this.loginFormGroup.controls[ControlName.password].untouched);   
  }

  loginInvalid(): boolean {
	const result = this.loginFormGroup.invalid;	
	//console.log(result);	
	return result;
  }

  signinInvalid(): boolean {
	const loginResult = this.loginInvalid();
	const signinResult = !this.loginFormGroup.controls[ControlName.MovieDbKey].value || (this.loginFormGroup.controls[ControlName.MovieDbKey].value as string).length < 2;	
	//console.log(loginResult+' '+signinResult);
	return loginResult || signinResult;
  }

  loginUser() {
	 this.modalMsg = '';
     this.userService.login(this.loginFormGroup.controls[ControlName.LoginName].value, 
     this.loginFormGroup.controls[ControlName.Password].value).subscribe((myTillNextLogin: number) => {
	    const res = myTillNextLogin <= 0;
	    this.tillNextLogin = myTillNextLogin;
        this.showModal = !res;        
        this.modalMsgType = MessageType.Error;
        this.modalMsg = res ? '' : $localize `:@@loginErrorMsg:Login Failed.`;
        this.loginClosed.emit(res);
     });
  }

  cancelUser() {
      this.loginFormGroup.controls[ControlName.LoginName].setValue('');
      this.loginFormGroup.controls[ControlName.Password].setValue('');
      this.loginFormGroup.controls[ControlName.MovieDbKey].setValue('');
      this.loginFormGroup.controls[ControlName.EmailAddress].setValue('');
      this.modalMsg = '';
  }

  signinUser() {
	 this.modalMsg = '';
     this.userService.signin(this.loginFormGroup.controls[ControlName.LoginName].value, 
        this.loginFormGroup.controls[ControlName.Password].value, 
        this.loginFormGroup.controls[ControlName.MovieDbKey].value).subscribe((res: boolean) =>{
          this.cancelUser();
          this.modalMsgType = res ? MessageType.Info : MessageType.Error;
          this.modalMsg = res ? $localize `:@@SigninSuccessMsg:Signin successful. Please Login.` : $localize `:@@SigninFailedMsg:Signin failed.`;
      });
  }
}
