import { ComponentFixture, TestBed, async, inject } from '@angular/core/testing';
import { MockBackend } from '@angular/http/testing';
import { Http, BaseRequestOptions } from '@angular/http';
import { OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { DateUtils, DataUtils } from 'ng-jhipster';
import { JhiLanguageService } from 'ng-jhipster';
import { MockLanguageService } from '../../../helpers/mock-language.service';
import { MockActivatedRoute } from '../../../helpers/mock-route.service';
import { OperinoComponentDetailComponent } from '../../../../../../main/webapp/app/entities/operino-component/operino-component-detail.component';
import { OperinoComponentService } from '../../../../../../main/webapp/app/entities/operino-component/operino-component.service';
import { OperinoComponent } from '../../../../../../main/webapp/app/entities/operino-component/operino-component.model';

describe('Component Tests', () => {

    describe('OperinoComponent Management Detail Component', () => {
        let comp: OperinoComponentDetailComponent;
        let fixture: ComponentFixture<OperinoComponentDetailComponent>;
        let service: OperinoComponentService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                declarations: [OperinoComponentDetailComponent],
                providers: [
                    MockBackend,
                    BaseRequestOptions,
                    DateUtils,
                    DataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    {
                        provide: Http,
                        useFactory: (backendInstance: MockBackend, defaultOptions: BaseRequestOptions) => {
                            return new Http(backendInstance, defaultOptions);
                        },
                        deps: [MockBackend, BaseRequestOptions]
                    },
                    {
                        provide: JhiLanguageService,
                        useClass: MockLanguageService
                    },
                    OperinoComponentService
                ]
            }).overrideComponent(OperinoComponentDetailComponent, {
                set: {
                    template: ''
                }
            }).compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(OperinoComponentDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(OperinoComponentService);
        });


        describe('OnInit', () => {
            it('Should call load all on init', () => {
            // GIVEN

            spyOn(service, 'find').and.returnValue(Observable.of(new OperinoComponent(10)));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.find).toHaveBeenCalledWith(123);
            expect(comp.operinoComponent).toEqual(jasmine.objectContaining({id:10}));
            });
        });
    });

});
