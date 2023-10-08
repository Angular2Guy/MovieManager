/**    Copyright 2019 Sven Loesekann
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
import { Component, DestroyRef, OnInit, inject } from "@angular/core";
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from "@angular/router";
import { Actor, Gender } from "../model/actor";
import { QueryParam } from "../model/common";
import { ActorsService } from "../services/actors.service";

@Component({
  selector: "app-actors",
  templateUrl: "./actors.component.html",
  styleUrls: ["./actors.component.scss"],
})
export class ActorsComponent implements OnInit {
  protected gender = Gender;
  protected actor: Actor = null;
  protected backParam = QueryParam.Empty;
  protected queryParam = QueryParam;
  private readonly destroy: DestroyRef = inject(DestroyRef);

  constructor(
    private route: ActivatedRoute,
    private actorService: ActorsService
  ) {}

  public ngOnInit() {
    this.actorService
      .findActorById(Number(this.route.snapshot.paramMap.get("id")))
      .pipe(takeUntilDestroyed(this.destroy))
      .subscribe((actor) => (this.actor = actor));
    this.backParam = !this.route.snapshot.queryParams?.back
      ? QueryParam.Empty
      : this.route.snapshot.queryParams?.back;
  }
}
