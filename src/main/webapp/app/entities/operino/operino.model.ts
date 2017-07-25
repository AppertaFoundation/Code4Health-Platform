import { User } from '../../shared';
import { OperinoComponent } from '../operino-component';
export class Operino {
    constructor(
        public id?: number,
        public name?: string,
        public active?: boolean,
        public provision?: boolean,
        public user?: User,
        public config?: {},
        public components?: OperinoComponent[]
    ) {
        provision = false;
    }
}
