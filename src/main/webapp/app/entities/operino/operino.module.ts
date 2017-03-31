import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { OperonCloudPlatformSharedModule } from '../../shared';
import { OperonCloudPlatformAdminModule } from '../../admin/admin.module';
import { OperinoComponentListComponent } from '../operino-component/operino-list-component.component';

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
        OperonCloudPlatformSharedModule,
        OperonCloudPlatformAdminModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        OperinoComponent,
        OperinoDetailComponent,
        OperinoDialogComponent,
        OperinoDeleteDialogComponent,
        OperinoPopupComponent,
        OperinoDeletePopupComponent,
        OperinoComponentListComponent
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
export class OperonCloudPlatformOperinoModule {}
