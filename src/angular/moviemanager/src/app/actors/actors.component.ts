import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, ParamMap } from '@angular/router';
import { Actor } from '../common/actor';
import { ActorsService } from '../services/actors.service';

@Component({
  selector: 'app-actors',
  templateUrl: './actors.component.html',
  styleUrls: ['./actors.component.scss']
})
export class ActorsComponent implements OnInit {

  actor: Actor = null;
    
  constructor(private route: ActivatedRoute, private router: Router, private actorService: ActorsService) { }

  ngOnInit() {
      this.actorService.findActorById(Number(this.route.snapshot.paramMap.get('id')))
          .subscribe(actor => this.actor = actor);
  }

}
