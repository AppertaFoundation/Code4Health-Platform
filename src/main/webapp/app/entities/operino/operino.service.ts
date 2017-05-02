import { Injectable } from '@angular/core';
import { Http, Response, URLSearchParams, BaseRequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Rx';

import { Operino } from './operino.model';
@Injectable()
export class OperinoService {

    private resourceUrl = 'api/operinos';
    private resourceSearchUrl = 'api/_search/operinos';

    constructor(private http: Http) { }

    create(operino: Operino): Observable<Operino> {
        let copy: Operino = Object.assign({}, operino);
        return this.http.post(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    update(operino: Operino): Observable<Operino> {
        let copy: Operino = Object.assign({}, operino);
        return this.http.put(this.resourceUrl, copy).map((res: Response) => {
            return res.json();
        });
    }

    find(id: number): Observable<Operino> {
        return this.http.get(`${this.resourceUrl}/${id}`).map((res: Response) => {
            return res.json();
        });
    }

    config(id: number): Observable<Object> {
        return this.http.get(`${this.resourceUrl}/${id}/config`).map((res: Response) => {
            return res.json();
        });
    }

    components(id: number): Observable<Response> {
        return this.http.get(`${this.resourceUrl}/${id}/components`);
    }

    query(req?: any): Observable<Response> {
        let options = this.createRequestOption(req);
        return this.http.get(this.resourceUrl, options)
        ;
    }

    delete(id: number): Observable<Response> {
        return this.http.delete(`${this.resourceUrl}/${id}`);
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
