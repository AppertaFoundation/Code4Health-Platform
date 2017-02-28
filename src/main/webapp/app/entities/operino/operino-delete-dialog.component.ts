import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, JhiLanguageService } from 'ng-jhipster';

import { Operino } from './operino.model';
import { OperinoPopupService } from './operino-popup.service';
import { OperinoService } from './operino.service';

@Component({
    selector: 'jhi-operino-delete-dialog',
    templateUrl: './operino-delete-dialog.component.html'
})
export class OperinoDeleteDialogComponent {

    operino: Operino;

    constructor(
        private jhiLanguageService: JhiLanguageService,
        private operinoService: OperinoService,
        public activeModal: NgbActiveModal,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['operino']);
    }

    clear () {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete (id: number) {
        this.operinoService.delete(id).subscribe(response => {
            this.eventManager.broadcast({
                name: 'operinoListModification',
                content: 'Deleted an operino'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-operino-delete-popup',
    template: ''
})
export class OperinoDeletePopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor (
        private route: ActivatedRoute,
        private operinoPopupService: OperinoPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe(params => {
            this.modalRef = this.operinoPopupService
                .open(OperinoDeleteDialogComponent, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
