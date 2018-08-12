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
