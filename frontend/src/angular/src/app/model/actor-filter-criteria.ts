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
import { Gender } from "./actor";
import { SearchPhrase } from "./search-phrase";

export class ActorFilterCriteria {
    public name: string = '';
    public gender: Gender = Gender.Unknown;
    public birthdayFrom: Date =  null;
    public birthdayTo: Date = null;
    public dead: boolean = false;
    public biography: string = '';
    public popularity: number = 0;
    public movieCharacter: string = '';
    public searchPhrase = new SearchPhrase();   
} 