import { Component } from '@angular/core';
import { EventManager, JhiLanguageService } from 'ng-jhipster';

@Component({
    selector: 'jhi-footer',
    templateUrl: './footer.component.html'
})
export class FooterComponent {
    constructor(
        private jhiLanguageService: JhiLanguageService
    ) {
        this.jhiLanguageService.setLocations(['footer']);
    }
}
