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
import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FilterActorsComponent } from "./filter-actors.component";
import {
  NgbDatepickerModule,
  NgbOffcanvasModule,
  NgbRatingModule,
} from "@ng-bootstrap/ng-bootstrap";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { FilterActorsRoutingModule } from "./filter-actors-routing.module";

@NgModule({
  declarations: [FilterActorsComponent],
  imports: [
    CommonModule,
    NgbOffcanvasModule,
    NgbDatepickerModule,
    NgbRatingModule,
    FormsModule,
    ReactiveFormsModule,
    FilterActorsRoutingModule,
  ],
})
export class FilterActorsModule {}
