import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { Response } from '@angular/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { EventManager, ParseLinks, PaginationUtil, JhiLanguageService, AlertService } from 'ng-jhipster';

import { OperinoComponent } from './operino-component.model';
import { Operino } from '../operino/operino.model';
import { OperinoComponentService } from './operino-component.service';
import { ITEMS_PER_PAGE, Principal } from '../../shared';
import { PaginationConfig } from '../../blocks/config/uib-pagination.config';

@Component({
    selector: 'jhi-operino-list-component',
    templateUrl: './operino-list-component.component.html'
})
export class OperinoComponentListComponent implements OnInit, OnDestroy {

    @Input() operino: Operino;
    operinoComponents: OperinoComponent[];
    currentAccount: any;
    eventSubscriber: Subscription;
    itemsPerPage: number;
    links: any;
    page: any;
    predicate: any;
    queryCount: any;
    reverse: any;
    totalItems: number;
    currentSearch: string;

    constructor(
        private jhiLanguageService: JhiLanguageService,
        private operinoComponentService: OperinoComponentService,
        private alertService: AlertService,
        private eventManager: EventManager,
        private parseLinks: ParseLinks,
        private activatedRoute: ActivatedRoute,
        private principal: Principal
    ) {
        this.operinoComponents = [];
        this.itemsPerPage = ITEMS_PER_PAGE;
        this.page = 0;
        this.links = {
            last: 0
        };
        this.predicate = 'id';
        this.reverse = true;
        this.currentSearch = activatedRoute.snapshot.params['search'] ? activatedRoute.snapshot.params['search'] : '';
        this.jhiLanguageService.setLocations(['operinoComponent', 'hostingType', 'operinoComponentType', 'operino', 'footer']);
    }

    loadAll () {
        if (this.currentSearch) {
            this.operinoComponentService.search({
                query: this.currentSearch,
                page: this.page,
                size: this.itemsPerPage,
                sort: this.sort()
            }).subscribe(
                (res: Response) => this.onSuccess(res.json(), res.headers),
                (res: Response) => this.onError(res.json())
            );
            return;
        }
        this.operinoComponentService.componentsForOperino(this.operino.id, {
            page: this.page,
            size: this.itemsPerPage,
            sort: this.sort()
        }).subscribe(
            (res: Response) => this.onSuccess(res.json(), res.headers),
            (res: Response) => this.onError(res.json())
        );
    }

    reset () {
        this.page = 0;
        this.operinoComponents = [];
        this.loadAll();
    }

    loadPage(page) {
        this.page = page;
        this.loadAll();
    }

    clear () {
        this.operinoComponents = [];
        this.links = {
            last: 0
        };
        this.page = 0;
        this.predicate = 'id';
        this.reverse = true;
        this.currentSearch = '';
        this.loadAll();
    }

    search (query) {
        if (!query) {
            return this.clear();
        }
        this.operinoComponents = [];
        this.links = {
            last: 0
        };
        this.page = 0;
        this.predicate = '_score';
        this.reverse = false;
        this.currentSearch = query;
        this.loadAll();
    }
    ngOnInit() {
        this.loadAll();
        this.principal.identity().then((account) => {
            this.currentAccount = account;
        });
        this.registerChangeInOperinoComponents();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId (index: number, item: OperinoComponent) {
        return item.id;
    }

    registerChangeInOperinoComponents() {
        this.eventSubscriber = this.eventManager.subscribe('operinoComponentListModification', (response) => this.reset());
    }

    sort () {
        let result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
        if (this.predicate !== 'id') {
            result.push('id');
        }
        return result;
    }

    private onSuccess(data, headers) {
        this.links = this.parseLinks.parse(headers.get('link'));
        this.totalItems = headers.get('X-Total-Count');
        for (let i = 0; i < data.length; i++) {
            this.operinoComponents.push(data[i]);
        }
        console.log("this.operinoComponents  = " , this.operinoComponents );
    }

    private onError (error) {
        this.alertService.error(error.message, null, null);
    }
}
