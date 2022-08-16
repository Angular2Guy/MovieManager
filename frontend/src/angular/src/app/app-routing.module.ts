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
import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { SearchComponent } from './search/search.component';
import { MoviesComponent } from './movies/movies.component';
import { ActorsComponent } from './actors/actors.component';
import { MovieImportComponent } from './movie-import/movie-import.component';

const routes: Routes = [
    {path: 'search', component: SearchComponent},
    {path: 'movie/:id', component: MoviesComponent},
    {path: 'actor/:id', component: ActorsComponent},
    {path: 'movie-import', component: MovieImportComponent},
    {path: 'filter-movies', loadChildren: () => import('./filter-movies/filter-movies.module')
       .then(m => m.FilterMoviesModule)},
       {path: 'filter-actors', loadChildren: () => import('./filter-actors/filter-actors.module')
       .then(m => m.FilterActorsModule)},
    {path: '**', redirectTo: '/search', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, { relativeLinkResolution: 'corrected' })],
  exports: [RouterModule]
})
export class AppRoutingModule { }
