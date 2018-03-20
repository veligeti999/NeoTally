import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {ApiProvider} from "../api/api";
import {API_CONFIG} from "../../app/app.config";
import {Counter} from "../../model/counter.model";

@Injectable()
export class AuthProvider {

  constructor(
    public http: HttpClient,

    private apiProvider: ApiProvider) {
    console.log('Hello AuthProvider Provider');
  }

  login(secret): Promise<Counter> {
    const authHeader = this.apiProvider.setAuthHeaders(secret);
    console.log(authHeader);
    return this.apiProvider.get('', authHeader, null, true).toPromise();
  }



}
