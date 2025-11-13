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
import { Routes } from "@angular/router";
import { SearchComponent } from "./search/search.component";

export const routes: Routes = [
  { path: "search", component: SearchComponent },
  { path: "movie/:id", loadComponent: () => import("./movies/movies.component").then(m => m.MoviesComponent) },
  { path: "actor/:id",  loadComponent: () => import("./actors/actors.component").then(m => m.ActorsComponent) },
  { path: "movie-import", loadComponent: () => import("./movie-import/movie-import.component").then(m => m.MovieImportComponent) },
  {
    path: "filter-movies",
    loadComponent: () =>
      import("./filter-movies/filter-movies.component").then(
        (m) => m.FilterMoviesComponent
      ),
  },
  {
    path: "filter-actors",
    loadComponent: () =>
      import("./filter-actors/filter-actors.component").then(
        (m) => m.FilterActorsComponent
      ),
  },
  { path: "**", redirectTo: "/search", pathMatch: "full" },
];
