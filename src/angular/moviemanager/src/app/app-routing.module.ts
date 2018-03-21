import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { SearchComponent } from './search/search.component';
import { MoviesComponent } from './movies/movies.component';
import { ActorsComponent } from './actors/actors.component';

const routes: Routes = [
    {path: 'search', component: SearchComponent},
    {path: 'movie/:id', component: MoviesComponent},
    {path: 'actor/:id', component: ActorsComponent},
    {path: '**', component: SearchComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
