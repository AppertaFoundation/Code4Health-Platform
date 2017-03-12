import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { EventManager, AlertService, JhiLanguageService } from 'ng-jhipster';
import { Operino } from './operino.model';
import { OperinoService } from './operino.service';
import { User, UserService } from '../../shared';

@Component({
    selector: 'jhi-operino-detail',
    templateUrl: './operino-detail.component.html'
})
export class OperinoDetailComponent implements OnInit, OnDestroy {

    operino: Operino;
    private subscription: any;
    authorities: any[];
    isSaving: boolean;

    constructor(
        private jhiLanguageService: JhiLanguageService,
        private operinoService: OperinoService,
        private route: ActivatedRoute,
        private alertService: AlertService
    ) {
        this.jhiLanguageService.setLocations(['operino']);
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe(params => {
            this.load(params['id']);
        });
    }

    load (id) {
        this.operinoService.find(id).subscribe(operino => {
            this.operino = operino;
        });
    }

    updateStatus() {
        this.operino.active = ! this.operino.active;
        this.save();
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
        //this.eventManager.broadcast({ name: 'operinoListModification', content: 'OK'});
        this.isSaving = false;
    }

    private onSaveError (error) {
        this.isSaving = false;
        this.onError(error);
    }

    private onError (error) {
        this.alertService.error(error.message, null, null);
    }

    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

}
