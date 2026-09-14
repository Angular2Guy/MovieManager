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
import {
  Component,
  OnInit,
  EventEmitter,
  Output,
  DestroyRef,
  inject,
  ChangeDetectionStrategy,
  signal,
} from "@angular/core";
import { UsersService } from "../services/users.service";
import {
  FormBuilder,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from "@angular/forms";
import { TokenService } from "ngx-simple-charts/base-service";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { CommonModule } from "@angular/common";

enum ControlName {
  LoginName = "loginName",
  Password = "password",
  MovieDbKey = "movieDbKey",
  EmailAddress = "emailAddress",
}

enum MessageType {
  Info = "info",
  Error = "error",
}

@Component({
  selector: "app-login",
  imports: [CommonModule, FormsModule, ReactiveFormsModule],
  templateUrl: "./login.component.html",
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ["./login.component.scss"],
})
export class LoginComponent implements OnInit {
  @Output() loginClosed = new EventEmitter<boolean>();
  protected ControlName = ControlName;
  protected MessageType = MessageType;
  protected showModal = signal(true);
  protected loginFormGroup: FormGroup;
  protected modalMsg = signal("");
  protected modalMsgType = signal(MessageType.Error);
  protected tillNextLogin = signal(0);
  protected waitingForResponse = signal(false);
  private readonly destroy: DestroyRef = inject(DestroyRef);

  constructor(
    private userService: UsersService,
    formBuilder: FormBuilder,
    private tokenService: TokenService,
  ) {
    this.loginFormGroup = formBuilder.group({
      [ControlName.LoginName]: [
        "",
        [Validators.required, Validators.minLength(2)],
      ],
      [ControlName.Password]: [
        "",
        [Validators.required, Validators.minLength(2)],
      ],
      [ControlName.MovieDbKey]: "",
      [ControlName.EmailAddress]: "",
    });
  }

  ngOnInit() {
    this.showModal.set(!this.tokenService.userId);
    this.loginFormGroup.markAllAsTouched();
  }

  loginInvalid(): boolean {
    const result = this.loginFormGroup.invalid;
    return result;
  }

  signinInvalid(): boolean {
    const loginResult = this.loginInvalid();
    const signinResult =
      !this.loginFormGroup.controls[ControlName.MovieDbKey].value ||
      (this.loginFormGroup.controls[ControlName.MovieDbKey].value as string)
        .length < 2;
    return loginResult || signinResult;
  }

  loginUser() {
    if (this.loginInvalid()) {
      return;
    }
    this.waitingForResponse.set(true);
    this.modalMsg.set("");
    this.userService
      .login(
        this.loginFormGroup.controls[ControlName.LoginName].value,
        this.loginFormGroup.controls[ControlName.Password].value,
      )
      .pipe(takeUntilDestroyed(this.destroy))
      .subscribe((myTillNextLogin: number) => {
        const res = myTillNextLogin <= 0;
        this.tillNextLogin.set(myTillNextLogin);
        this.showModal.set(!res);
        this.modalMsgType.set(MessageType.Error);
        this.modalMsg.set(res ? "" : $localize`:@@loginErrorMsg:Login Failed.`);
        this.loginClosed.emit(res);
        this.waitingForResponse.set(false);
      });
  }

  cancelUser() {
    this.loginFormGroup.controls[ControlName.LoginName].setValue("");
    this.loginFormGroup.controls[ControlName.Password].setValue("");
    this.loginFormGroup.controls[ControlName.MovieDbKey].setValue("");
    this.loginFormGroup.controls[ControlName.EmailAddress].setValue("");
    this.modalMsg.set("");
  }

  signinUser() {
    this.modalMsg.set("");
    this.waitingForResponse.set(true);
    this.userService
      .signin(
        this.loginFormGroup.controls[ControlName.LoginName].value,
        this.loginFormGroup.controls[ControlName.Password].value,
        this.loginFormGroup.controls[ControlName.MovieDbKey].value,
      )
      .pipe(takeUntilDestroyed(this.destroy))
      .subscribe((res: boolean) => {
        this.cancelUser();
        this.modalMsgType.set(res ? MessageType.Info : MessageType.Error);
        this.modalMsg.set(res
          ? $localize`:@@SigninSuccessMsg:Signin successful. Please Login.`
          : $localize`:@@SigninFailedMsg:Signin failed.`);
        this.waitingForResponse.set(false);
      });
  }
}
