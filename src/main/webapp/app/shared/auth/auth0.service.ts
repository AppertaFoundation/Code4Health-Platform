import { Injectable } from '@angular/core';
import { tokenNotExpired } from 'angular2-jwt';
import { Router } from '@angular/router';

import { LoginModalService } from '../login/login-modal.service';
import { Principal } from './principal.service';
import { StateStorageService } from './state-storage.service';

// Avoid name not found warnings
declare var auth0: any;

@Injectable()
export class Auth {

    // Configure Auth0
    auth0 = new auth0.WebAuth({
        domain: 'noesisinfo.eu.auth0.com',
        clientID: '5BRuTaQzilCdIebETGxgx5DZVOFGvFmw',
        // specify your desired callback URL
        callbackURL: 'http://localhost:3000',
        responseType: 'token id_token'
    });

    constructor(
        private principal: Principal,
        private stateStorageService: StateStorageService,
        private loginModalService: LoginModalService,
        private router: Router
    ) {}

    public handleAuthentication(): void {
        this.auth0.parseHash((err, authResult) => {
            if (authResult && authResult.accessToken && authResult.idToken) {
                window.location.hash = '';
                localStorage.setItem('access_token', authResult.accessToken);
                localStorage.setItem('id_token', authResult.idToken);
                this.router.navigate(['/home']);
            } else if (authResult && authResult.error) {
                alert('Error: ' + authResult.error);
            }
        });
    }

    public login(username: string, password: string): void {
        this.auth0.client.login({
            realm: 'Username-Password-Authentication',
            username,
            password
        }, (err, authResult) => {
            if (err) {
                alert('Error: ' + err.description);
                return;
            }
            if (authResult && authResult.idToken && authResult.accessToken) {
                this.setUser(authResult);
                this.router.navigate(['/home']);
            }
        });
    }

    public signup(email, password): void {
        this.auth0.redirect.signupAndLogin({
            connection: 'Username-Password-Authentication',
            email,
            password,
        }, function(err) {
            if (err) {
                alert('Error: ' + err.description);
            }
        });
    }

    public loginWithGoogle(): void {
        this.auth0.authorize({
            connection: 'google-oauth2',
        });
    }

    public isAuthenticated(): boolean {
        // Check whether the id_token is expired or not
        return tokenNotExpired();
    }

    public logout(): void {
        // Remove token from localStorage
        localStorage.removeItem('access_token');
        localStorage.removeItem('id_token');
    }

    private setUser(authResult): void {
        localStorage.setItem('access_token', authResult.accessToken);
        localStorage.setItem('id_token', authResult.idToken);
    }

    authorize (force) {
        let authReturn = this.principal.identity(force).then(authThen.bind(this));

        return authReturn;

        function authThen () {
            //let isAuthenticated = this.principal.isAuthenticated();
            let isAuthenticated = tokenNotExpired();
            let toStateInfo = this.stateStorageService.getDestinationState().destination;

            // an authenticated user can't access to login and register pages
            if (isAuthenticated && (toStateInfo.name === 'register' || toStateInfo.name === 'social-auth')) {
                this.router.navigate(['']);
                return false;
            }

            // recover and clear previousState after external login redirect (e.g. oauth2)
            let fromStateInfo = this.stateStorageService.getDestinationState().from;
            let previousState = this.stateStorageService.getPreviousState();
            if (isAuthenticated && !fromStateInfo.name && previousState) {
                this.stateStorageService.resetPreviousState();
                this.router.navigate([previousState.name], { queryParams:  previousState.params  });
                return false;
            }

            if (toStateInfo.data.authorities && toStateInfo.data.authorities.length > 0) {
                return this.principal.hasAnyAuthority(toStateInfo.data.authorities).then(hasAnyAuthority => {
                    if (!hasAnyAuthority) {
                        if (isAuthenticated) {
                            // user is signed in but not authorized for desired state
                            this.router.navigate(['accessdenied']);
                        } else {
                            // user is not authenticated. Show the state they wanted before you
                            // send them to the login service, so you can return them when you're done
                            let toStateParamsInfo = this.stateStorageService.getDestinationState().params;
                            this.stateStorageService.storePreviousState(toStateInfo.name, toStateParamsInfo);
                            // now, send them to the signin state so they can log in
                            this.router.navigate(['accessdenied']).then(() => {
                                this.loginModalService.open();
                            });
                        }
                    }
                    return hasAnyAuthority;
                });
            }
            return true;
        }
    }
}