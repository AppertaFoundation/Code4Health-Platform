import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { Operino } from './operino.model';
import { OperinoService } from './operino.service';

@Component({
    selector: 'jhi-operino-detail',
    templateUrl: './operino-detail.component.html'
})
export class OperinoDetailComponent implements OnInit, OnDestroy {

    operino: Operino;
    private subscription: any;

    constructor(
        private jhiLanguageService: JhiLanguageService,
        private operinoService: OperinoService,
        private route: ActivatedRoute
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
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

}
