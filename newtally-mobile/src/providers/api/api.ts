import {HttpClient, HttpHeaders} from '@angular/common/http';
import { Injectable } from '@angular/core';
import {API_CONFIG} from "../../app/app.config";
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import {Observable} from "rxjs/Observable";
import {Events} from "ionic-angular";
import {AlertProvider} from "../alert/alert";
import {Network} from "@ionic-native/network";


export enum networkErrors {
  UNAUTHORIZED = 401,
  NO_INTERNET = -1,
  SERVER_ERROR = 500
}
@Injectable()
export class ApiProvider {
  headers: HttpHeaders;
  constructor(
    public http: HttpClient,
    private network: Network,
    private events: Events,
    private alert: AlertProvider) {
  }

  get(url, headers?, params?, withCredentials: boolean = false) {
    if (this.network.type === 'none') {
      throw {
        status: networkErrors.NO_INTERNET,
        message: 'Not connected to internet'
      };
    }
    let options: any = {
      // withCredentials
    };
    if (headers) {
      options.headers = headers;
    }
    if (params) {
      options.params = params;
    }

    console.log('options', options);
    // API_CONFIG.COUNTER_ENDPOINT +
    return this.http.get(API_CONFIG.COUNTER_ENDPOINT + url, options).map((res: any) => {
      console.log('res', res);
      return res.response_data;
    }).catch(this.handleError.bind(this));
  }

  post(url, params) {
    if (this.network.type === 'none') {
      throw {
        status: networkErrors.NO_INTERNET,
        message: 'Not connected to internet'
      };
    }
    const headers = new HttpHeaders({'Content-Type': 'application/json'});
    // headers.append('Content-Type', 'application/json');
    console.log(params);

    return this.http.post(API_CONFIG.COUNTER_ENDPOINT +  url, params, {headers}).map((res:any) => {
      console.log('res', res);
      return res.response_data;
    }).catch(this.handleError.bind(this));
  }

  put(url, params) {
    if (this.network.type === 'none') {
      throw {
        status: networkErrors.NO_INTERNET,
        message: 'Not connected to internet'
      };
    }
    return this.http.put(API_CONFIG.COUNTER_ENDPOINT +  url, params).map((res:any) => res.response_data)
      .catch(this.handleError.bind(this));
  }

  /**
   * Set basic auth header
   * @param secret
   * @returns {HttpHeaders}
   */
  setAuthHeaders(secret) {
    const username = 'bctr:' + secret,
          password = secret,
          toBtoa = btoa(`${username}:${password}`),
          headers = new HttpHeaders({'Authorization': `Basic ${toBtoa}`, 'Content-Type':'application/json'});

    // headers.set('Authorization', `Basic ${toBtoa}`);
    console.log(headers);
    // headers.append('Content-Type', 'application/x-www-form-urlencoded');
    return headers;
  }


  getCookie() {
    console.log('coookies', document.cookie);
  }

  handleError(error) {
    // console.log('error',error);
    // console.log(error.status);
    if (error.status === networkErrors.UNAUTHORIZED) {
      // logout the user
      this.events.publish('network:unauthorized');
      return;
    }
    this.alert.showWithTitle('Uh-Oh! Something went wrong. Please try again later.', 'Error');
    return Observable.throw(error);
    // if (error.status === networkErrors.NO_INTERNET) {
    //   alert(error.message);
    // }
    // switch(error.status) {
    //   case networkErrors.UNAUTHORIZED:
    //     this.events.publish('network:unauthorized');
    //     break;
    //   case networkErrors.SERVER_ERROR:
    //     this.alert.showWithTitle('Uh-Oh! Something went wrong. Please try again later.', 'Error');
    //     break;
    //   case networkErrors.NO_INTERNET:
    //     console.log(error.status);
    //     alert(error.message);
    //     this.alert.showWithTitle(error.message, 'Error');
    // }

    // return Observable.throw(error);
  }

}
