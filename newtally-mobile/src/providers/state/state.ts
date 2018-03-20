import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {Counter} from "../../model/counter.model";

@Injectable()
export class StateProvider {
  currentCounter: Counter;
  pushToken:string;
  constructor(public http: HttpClient) {
    console.log('Hello StateProvider Provider');
  }
}
