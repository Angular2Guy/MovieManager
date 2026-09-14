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
  DestroyRef,
  OnInit,
  inject,
  ChangeDetectionStrategy,
  signal,
} from "@angular/core";
import { Router } from "@angular/router";
import {
  NgbDatepickerModule,
  NgbDateStruct,
  NgbOffcanvas,
  NgbOffcanvasModule,
  NgbPopoverModule,
  NgbRatingConfig,
  NgbRatingModule,
  OffcanvasDismissReasons,
} from "@ng-bootstrap/ng-bootstrap";
import { Actor, Gender } from "../model/actor";
import { ActorFilterCriteria } from "../model/actor-filter-criteria";
import { FulltextFilter, QueryParam } from "../model/common";
import { ActorsService } from "../services/actors.service";
import { takeUntilDestroyed } from "@angular/core/rxjs-interop";
import { Operator, SearchString } from "../model/search-string";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { CommonModule } from "@angular/common";

@Component({
  selector: "app-filter-actors",
  imports: [
    CommonModule,
    NgbOffcanvasModule,
    NgbDatepickerModule,
    NgbRatingModule,
    NgbPopoverModule,
    FormsModule,
    ReactiveFormsModule,
  ],
  templateUrl: "./filter-actors.component.html",
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ["./filter-actors.component.scss"],
})
export class FilterActorsComponent implements OnInit {
  protected gender = Gender;
  protected filtering = signal(false);
  protected filteredActors = signal<Actor[]>([]);
  protected ngbBirthdayFrom = signal<NgbDateStruct | null>(null);
  protected ngbBirthdayTo = signal<NgbDateStruct | null>(null);
  protected closeResult = signal("");
  protected FullTextFilter = FulltextFilter;
  protected filterType = signal(FulltextFilter.PhraseFilter);
  protected name = signal("");
  protected actorGender = signal(Gender.Unknown);
  protected dead = signal(false);
  protected popularity = signal(0);
  protected movieCharacter = signal("");
  protected phrase = signal("");
  protected otherWordsInPhrase = signal<number | null>(null);
  protected searchWords = "";
  private readonly destroy: DestroyRef = inject(DestroyRef);

  constructor(
    private actorsService: ActorsService,
    private router: Router,
    private offcanvasService: NgbOffcanvas,
    public ngbRatingConfig: NgbRatingConfig,
  ) {}

  public ngOnInit(): void {
    this.ngbRatingConfig.max = 10;
  }

  public open(content: unknown) {
    this.otherWordsInPhrase.set(null);
    this.offcanvasService
      .open(content, { ariaLabelledBy: "offcanvas-basic-title" })
      .result.then(
        (result) => {
          this.closeResult.set(`Closed with: ${result}`);
        },
        (reason) => {
          this.closeResult.set(`Dismissed ${this.getDismissReason(reason)}`);
        },
      );
  }

  public back() {
    this.router.navigate(["search"]);
  }

  public selectActor(actor: Actor): void {
    this.router.navigate(["actor", actor.id], {
      queryParams: { back: QueryParam.ActorsBack },
    });
  }

  public resetFilters(): void {
    this.name.set("");
    this.actorGender.set(Gender.Unknown);
    this.dead.set(false);
    this.popularity.set(0);
    this.movieCharacter.set("");
    this.phrase.set("");
    this.otherWordsInPhrase.set(null);
    this.searchWords = "";
    this.closeResult.set("");
  }

  public showFilterMovies(): void {
    this.router.navigate(["/filter-movies"]);
  }

  public switchFilters(): void {
    this.phrase.set("");
    this.otherWordsInPhrase.set(null);
    this.filterType.set(
      this.filterType() === this.FullTextFilter.PhraseFilter
        ? this.FullTextFilter.WordFilter
        : this.FullTextFilter.PhraseFilter,
    );
  }

  private getDismissReason(reason: unknown): void {
    if (reason === OffcanvasDismissReasons.ESC) {
      return this.resetFilters();
    } else {
      this.filtering.set(true);
      const criteria = this.buildCriteria();
      this.actorsService
        .findActorsByCriteria(criteria)
        .pipe(takeUntilDestroyed(this.destroy))
        .subscribe({
          next: (result) => {
            this.filteredActors.set(result);
            this.filtering.set(false);
          },
          error: (failed) => {
            console.log(failed);
            this.filtering.set(false);
            this.router.navigate(["/"]);
          },
        });
    }
  }

  private buildCriteria(): ActorFilterCriteria {
    const criteria = new ActorFilterCriteria();
    criteria.name = this.name();
    criteria.gender = this.actorGender();
    criteria.dead = this.dead();
    criteria.popularity = this.popularity();
    criteria.movieCharacter = this.movieCharacter();
    criteria.birthdayFrom = !this.ngbBirthdayFrom()
      ? null
      : new Date(
          this.ngbBirthdayFrom()!.year,
          this.ngbBirthdayFrom()!.month,
          this.ngbBirthdayFrom()!.day,
        );
    criteria.birthdayTo = !this.ngbBirthdayTo()
      ? null
      : new Date(
          this.ngbBirthdayTo()!.year,
          this.ngbBirthdayTo()!.month,
          this.ngbBirthdayTo()!.day,
        );
    criteria.searchTerm.searchPhrase.phrase = this.phrase();
    criteria.searchTerm.searchPhrase.otherWordsInPhrase =
      this.otherWordsInPhrase();
    criteria.searchTerm.searchStrings = this.createSearchStrings();
    return criteria;
  }

  private createSearchStrings(): SearchString[] {
    const opsMap = new Map([
      [Operator.AND.toString(), Operator.AND],
      [Operator.NOT.toString(), Operator.NOT],
      [Operator.OR.toString(), Operator.OR],
    ]);
    const searchWords = this.searchWords
      .split(" ")
      .map((str) => str.trim())
      .filter((str) => !!opsMap.get(str[0]) && str.length > 4)
      .map(
        (str) => new SearchString(str.substring(1).trim(), opsMap.get(str[0])),
      );
    return searchWords;
  }
}
