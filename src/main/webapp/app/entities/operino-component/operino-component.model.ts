
const enum HostingType {
    'N3',
    ' NON_N3'

};

const enum OperinoComponentType {
    'CDR',
    ' DEMOGRAPHICS',
    ' TERMINOLOGY'

};
import { Operino } from '../operino';
export class OperinoComponent {
    constructor(
        public id?: number,
        public hosting?: HostingType,
        public availability?: boolean,
        public applyLimits?: boolean,
        public recordsNumber?: number,
        public transactionsLimit?: number,
        public diskSpace?: number,
        public computeResourceLimit?: number,
        public type?: OperinoComponentType,
        public operino?: Operino,
    ) { }
}
