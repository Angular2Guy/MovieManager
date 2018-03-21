import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';

import { AppComponent } from './app.component';
import { MoviesComponent } from './movies/movies.component';
import { ActorsComponent } from './actors/actors.component';
import { ActorsService } from './services/actors.service';
import { MoviesService } from './services/movies.service';
import { UsersService } from './services/users.service';
import { SearchComponent } from './search/search.component';
import { LoginComponent } from './login/login.component';


@NgModule({
  declarations: [
    AppComponent,
    MoviesComponent,
    ActorsComponent,
    SearchComponent,
    LoginComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [
    ActorsService,
    MoviesService,
    UsersService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
