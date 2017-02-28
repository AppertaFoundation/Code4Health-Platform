import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { Code4HealthplatformOperinoModule } from './operino/operino.module';
import { Code4HealthplatformOperinoComponentModule } from './operino-component/operino-component.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    imports: [
        Code4HealthplatformOperinoModule,
        Code4HealthplatformOperinoComponentModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class Code4HealthplatformEntityModule {}
