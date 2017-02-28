import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, AlertService, JhiLanguageService } from 'ng-jhipster';

import { Operino } from './operino.model';
import { OperinoPopupService } from './operino-popup.service';
import { OperinoService } from './operino.service';
import { User, UserService } from '../../shared';
import { OperinoComponent, OperinoComponentService } from '../operino-component';
@Component({
    selector: 'jhi-operino-dialog',
    templateUrl: './operino-dialog.component.html'
})
export class OperinoDialogComponent implements OnInit {

    operino: Operino;
    authorities: any[];
    isSaving: boolean;

    users: User[];

    operinocomponents: OperinoComponent[];
    constructor(
        public activeModal: NgbActiveModal,
        private jhiLanguageService: JhiLanguageService,
        private alertService: AlertService,
        private operinoService: OperinoService,
        private userService: UserService,
        private operinoComponentService: OperinoComponentService,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['operino']);
    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        this.userService.query().subscribe(
            (res: Response) => { this.users = res.json(); }, (res: Response) => this.onError(res.json()));
        this.operinoComponentService.query().subscribe(
            (res: Response) => { this.operinocomponents = res.json(); }, (res: Response) => this.onError(res.json()));
    }
    clear () {
        this.activeModal.dismiss('cancel');
    }

    save () {
        this.isSaving = true;
        if (this.operino.id !== undefined) {
            this.operinoService.update(this.operino)
                .subscribe((res: Operino) => this.onSaveSuccess(res), (res: Response) => this.onSaveError(res.json()));
        } else {
            this.operinoService.create(this.operino)
                .subscribe((res: Operino) => this.onSaveSuccess(res), (res: Response) => this.onSaveError(res.json()));
        }
    }

    private onSaveSuccess (result: Operino) {
        this.eventManager.broadcast({ name: 'operinoListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError (error) {
        this.isSaving = false;
        this.onError(error);
    }

    private onError (error) {
        this.alertService.error(error.message, null, null);
    }

    trackUserById(index: number, item: User) {
        return item.id;
    }

    trackOperinoComponentById(index: number, item: OperinoComponent) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-operino-popup',
    template: ''
})
export class OperinoPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor (
        private route: ActivatedRoute,
        private operinoPopupService: OperinoPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe(params => {
            if ( params['id'] ) {
                this.modalRef = this.operinoPopupService
                    .open(OperinoDialogComponent, params['id']);
            } else {
                this.modalRef = this.operinoPopupService
                    .open(OperinoDialogComponent);
            }

        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
