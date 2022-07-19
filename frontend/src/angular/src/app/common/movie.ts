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
import { Genere } from './genere';
import { Cast } from './cast';

export interface Movie {
    id: number;
    overview: string;
    release_date: Date;
    title: string;
    num?: number;
    movie_id: number;
    runtime: number;
    revenue: number;
    vote_average: number;
    vote_count: number;
    budget: number;
	genres: Genere[];
    myCast: Cast[];
    myGenere: Genere[];
}
