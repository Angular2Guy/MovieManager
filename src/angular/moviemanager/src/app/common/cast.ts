import { Movie } from './movie';
import { Actor } from './actor';

export interface Cast {
    id: number;
    character: string;
    name: string;
    myMovie: Movie;
    myActor: Actor;
}