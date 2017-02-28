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
import { OperinoDetailComponent } from '../../../../../../main/webapp/app/entities/operino/operino-detail.component';
import { OperinoService } from '../../../../../../main/webapp/app/entities/operino/operino.service';
import { Operino } from '../../../../../../main/webapp/app/entities/operino/operino.model';

describe('Component Tests', () => {

    describe('Operino Management Detail Component', () => {
        let comp: OperinoDetailComponent;
        let fixture: ComponentFixture<OperinoDetailComponent>;
        let service: OperinoService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                declarations: [OperinoDetailComponent],
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
                    OperinoService
                ]
            }).overrideComponent(OperinoDetailComponent, {
                set: {
                    template: ''
                }
            }).compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(OperinoDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(OperinoService);
        });


        describe('OnInit', () => {
            it('Should call load all on init', () => {
            // GIVEN

            spyOn(service, 'find').and.returnValue(Observable.of(new Operino(10)));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.find).toHaveBeenCalledWith(123);
            expect(comp.operino).toEqual(jasmine.objectContaining({id:10}));
            });
        });
    });

});
