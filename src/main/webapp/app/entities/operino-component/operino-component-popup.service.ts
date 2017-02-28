import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { OperinoComponent } from './operino-component.model';
import { OperinoComponentService } from './operino-component.service';
@Injectable()
export class OperinoComponentPopupService {
    private isOpen = false;
    constructor (
        private modalService: NgbModal,
        private router: Router,
        private operinoComponentService: OperinoComponentService

    ) {}

    open (component: Component, id?: number | any): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;

        if (id) {
            this.operinoComponentService.find(id).subscribe(operinoComponent => {
                this.operinoComponentModalRef(component, operinoComponent);
            });
        } else {
            return this.operinoComponentModalRef(component, new OperinoComponent());
        }
    }

    operinoComponentModalRef(component: Component, operinoComponent: OperinoComponent): NgbModalRef {
        let modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.operinoComponent = operinoComponent;
        modalRef.result.then(result => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.isOpen = false;
        }, (reason) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.isOpen = false;
        });
        return modalRef;
    }
}
