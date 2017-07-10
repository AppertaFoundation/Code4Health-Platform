import { Injectable } from '@angular/core';
import { Http, Response, URLSearchParams, BaseRequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Rx';

import { OperinoComponent } from './operino-component.model';
@Injectable()
export class OperinoComponentService {

    private resourceUrl = 'api/operino-components';
    private operinoResourceUrl = 'api/operinos';
    private resourceSearchUrl = 'api/_search/operino-components';

    constructor(private http: Http) { }

    create(operinoComponent: OperinoComponent): Observable<OperinoComponent> {
        let copy: OperinoComponent = Object.assign({}, operinoComponent);
        if(operinoComponent.operino != null) {
            return this.http.post(`${this.operinoResourceUrl}/${operinoComponent.operino.id}/components`, copy).map((res: Response) => {
                return res.json();
            });
        } else {
            return this.http.post(this.resourceUrl, copy).map((res: Response) => {
                return res.json();
            });
        }
    }

    update(operinoComponent: OperinoComponent): Observable<OperinoComponent> {
        let copy: OperinoComponent = Object.assign({}, operinoComponent);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    find(id: number): Observable<OperinoComponent> {
        return this.http.get(`${this.resourceUrl}/${id}`).map((res: Response) => {
            return res.json();
        });
    }

    query(req?: any): Observable<Response> {
        let options = this.createRequestOption(req);
        return this.http.get(this.resourceUrl, options);
    }

    componentsForOperino(id: number, req?: any): Observable<Response> {
        let options = this.createRequestOption(req);
        return this.http.get(`${this.operinoResourceUrl}/${id}/components`, options);
    }

    delete(componentId: number, operinoId: number): Observable<Response> {
        //return this.http.delete(`${this.resourceUrl}/${id}`);
        return this.http.delete(`${this.operinoResourceUrl}/${operinoId}/components/${componentId}`);
    }

    search(req?: any): Observable<Response> {
        let options = this.createRequestOption(req);
        return this.http.get(this.resourceSearchUrl, options)
        ;
    }

    private createRequestOption(req?: any): BaseRequestOptions {
        let options: BaseRequestOptions = new BaseRequestOptions();
        if (req) {
            let params: URLSearchParams = new URLSearchParams();
            params.set('page', req.page);
            params.set('size', req.size);
            if (req.sort) {
                params.paramsMap.set('sort', req.sort);
            }
            params.set('query', req.query);

            options.search = params;
        }
        return options;
    }
}
