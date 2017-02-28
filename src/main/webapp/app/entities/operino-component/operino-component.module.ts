import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { Code4HealthplatformSharedModule } from '../../shared';

import {
    OperinoComponentService,
    OperinoComponentPopupService,
    OperinoComponentComponent,
    OperinoComponentDetailComponent,
    OperinoComponentDialogComponent,
    OperinoComponentPopupComponent,
    OperinoComponentDeletePopupComponent,
    OperinoComponentDeleteDialogComponent,
    operinoComponentRoute,
    operinoComponentPopupRoute,
} from './';

let ENTITY_STATES = [
    ...operinoComponentRoute,
    ...operinoComponentPopupRoute,
];

@NgModule({
    imports: [
        Code4HealthplatformSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        OperinoComponentComponent,
        OperinoComponentDetailComponent,
        OperinoComponentDialogComponent,
        OperinoComponentDeleteDialogComponent,
        OperinoComponentPopupComponent,
        OperinoComponentDeletePopupComponent,
    ],
    entryComponents: [
        OperinoComponentComponent,
        OperinoComponentDialogComponent,
        OperinoComponentPopupComponent,
        OperinoComponentDeleteDialogComponent,
        OperinoComponentDeletePopupComponent,
    ],
    providers: [
        OperinoComponentService,
        OperinoComponentPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class Code4HealthplatformOperinoComponentModule {}
