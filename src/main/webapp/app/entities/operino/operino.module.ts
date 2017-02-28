import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { Code4HealthplatformSharedModule } from '../../shared';
import { Code4HealthplatformAdminModule } from '../../admin/admin.module';

import {
    OperinoService,
    OperinoPopupService,
    OperinoComponent,
    OperinoDetailComponent,
    OperinoDialogComponent,
    OperinoPopupComponent,
    OperinoDeletePopupComponent,
    OperinoDeleteDialogComponent,
    operinoRoute,
    operinoPopupRoute,
} from './';

let ENTITY_STATES = [
    ...operinoRoute,
    ...operinoPopupRoute,
];

@NgModule({
    imports: [
        Code4HealthplatformSharedModule,
        Code4HealthplatformAdminModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        OperinoComponent,
        OperinoDetailComponent,
        OperinoDialogComponent,
        OperinoDeleteDialogComponent,
        OperinoPopupComponent,
        OperinoDeletePopupComponent,
    ],
    entryComponents: [
        OperinoComponent,
        OperinoDialogComponent,
        OperinoPopupComponent,
        OperinoDeleteDialogComponent,
        OperinoDeletePopupComponent,
    ],
    providers: [
        OperinoService,
        OperinoPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class Code4HealthplatformOperinoModule {}
