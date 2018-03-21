import { Genere } from './genere';
import { Cast } from './cast';

export interface Movie {
    id: number;
    overview: string;
    release_date: Date;
    title: string;
    num: number;
    myCast: Cast[];
    myGenere: Genere[];
}