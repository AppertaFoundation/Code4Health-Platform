import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { PaginationUtil } from 'ng-jhipster';

import { OperinoComponentComponent } from './operino-component.component';
import { OperinoComponentDetailComponent } from './operino-component-detail.component';
import { OperinoComponentPopupComponent } from './operino-component-dialog.component';
import { OperinoComponentDeletePopupComponent } from './operino-component-delete-dialog.component';

import { Principal } from '../../shared';


export const operinoComponentRoute: Routes = [
  {
    path: 'operino-component',
    component: OperinoComponentComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'code4HealthplatformApp.operinoComponent.home.title'
    }
  }, {
    path: 'operino-component/:id',
    component: OperinoComponentDetailComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'code4HealthplatformApp.operinoComponent.home.title'
    }
  }
];

export const operinoComponentPopupRoute: Routes = [
  {
    path: 'operino-component-new',
    component: OperinoComponentPopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'code4HealthplatformApp.operinoComponent.home.title'
    },
    outlet: 'popup'
  },
  {
    path: 'operino-component/:id/edit',
    component: OperinoComponentPopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'code4HealthplatformApp.operinoComponent.home.title'
    },
    outlet: 'popup'
  },
  {
    path: 'operino-component/:id/delete',
    component: OperinoComponentDeletePopupComponent,
    data: {
        authorities: ['ROLE_USER'],
        pageTitle: 'code4HealthplatformApp.operinoComponent.home.title'
    },
    outlet: 'popup'
  }
];
