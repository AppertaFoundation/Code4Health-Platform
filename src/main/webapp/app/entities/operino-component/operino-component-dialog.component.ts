import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, AlertService, JhiLanguageService } from 'ng-jhipster';

import { OperinoComponent } from './operino-component.model';
import { OperinoComponentPopupService } from './operino-component-popup.service';
import { OperinoComponentService } from './operino-component.service';
import { Operino, OperinoService } from '../operino';
@Component({
    selector: 'jhi-operino-component-dialog',
    templateUrl: './operino-component-dialog.component.html'
})
export class OperinoComponentDialogComponent implements OnInit {

    operinoComponent: OperinoComponent;
    authorities: any[];
    isSaving: boolean;

    operinos: Operino[];
    constructor(
        public activeModal: NgbActiveModal,
        private jhiLanguageService: JhiLanguageService,
        private alertService: AlertService,
        private operinoComponentService: OperinoComponentService,
        private operinoService: OperinoService,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['operinoComponent', 'hostingType', 'operinoComponentType']);
    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        this.operinoService.query().subscribe(
            (res: Response) => { this.operinos = res.json(); }, (res: Response) => this.onError(res.json()));
    }
    clear () {
        this.activeModal.dismiss('cancel');
    }

    save () {
        this.isSaving = true;
        if (this.operinoComponent.id !== undefined) {
            this.operinoComponentService.update(this.operinoComponent)
                .subscribe((res: OperinoComponent) => this.onSaveSuccess(res), (res: Response) => this.onSaveError(res.json()));
        } else {
            this.operinoComponentService.create(this.operinoComponent)
                .subscribe((res: OperinoComponent) => this.onSaveSuccess(res), (res: Response) => this.onSaveError(res.json()));
        }
    }

    private onSaveSuccess (result: OperinoComponent) {
        this.eventManager.broadcast({ name: 'operinoComponentListModification', content: 'OK'});
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

    trackOperinoById(index: number, item: Operino) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-operino-component-popup',
    template: ''
})
export class OperinoComponentPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor (
        private route: ActivatedRoute,
        private operinoComponentPopupService: OperinoComponentPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe(params => {
            if ( params['id'] ) {
                this.modalRef = this.operinoComponentPopupService
                    .open(OperinoComponentDialogComponent, params['id']);
            } else {
                this.modalRef = this.operinoComponentPopupService
                    .open(OperinoComponentDialogComponent);
            }

        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
