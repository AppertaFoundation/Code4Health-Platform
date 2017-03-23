import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiLanguageService } from 'ng-jhipster';
import { OperinoComponent } from './operino-component.model';
import { OperinoComponentService } from './operino-component.service';

@Component({
    selector: 'jhi-operino-component-detail',
    templateUrl: './operino-component-detail.component.html'
})
export class OperinoComponentDetailComponent implements OnInit, OnDestroy {

    operinoComponent: OperinoComponent;
    private subscription: any;

    constructor(
        private jhiLanguageService: JhiLanguageService,
        private operinoComponentService: OperinoComponentService,
        private route: ActivatedRoute
    ) {
        this.jhiLanguageService.setLocations(['operinoComponent', 'hostingType', 'operinoComponentType', 'footer', 'operino']);
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe(params => {
            this.load(params['id']);
        });
    }

    load (id) {
        this.operinoComponentService.find(id).subscribe(operinoComponent => {
            this.operinoComponent = operinoComponent;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
    }

}
