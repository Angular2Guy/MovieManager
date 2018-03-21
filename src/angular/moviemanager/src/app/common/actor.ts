import { Cast } from './cast';

export interface Actor {
    id: number;
    name: string;
    gender: number;
    birthday: Date;
    deathday: Date;
    biography: string;
    place_of_birth: string;
    myCasts: Cast[];
}