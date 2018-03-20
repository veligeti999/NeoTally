import { Injectable } from '@angular/core';
import {ApiProvider} from "../api/api";
import {API_CONFIG} from "../../app/app.config";
import {DiscountParams} from "../../model/discount.params";
import {QrCodeParams} from "../../model/qrcode.params";
import {OrderSubmitParams} from "../../model/submitOrder.params";
import {QrCode} from "../../model/qrCode.model";

@Injectable()
export class CounterProvider {

  constructor(private apiProvider: ApiProvider) {
    console.log('Hello CounterProvider Provider');
  }

  calculateDiscount(params: DiscountParams) {
    return this.apiProvider.post(API_CONFIG.DISCOUNT, params).toPromise();
  }

  getQrCode(params: QrCodeParams): Promise<QrCode> {
    return this.apiProvider.post(API_CONFIG.QR_CODE, params).toPromise();
  }

  submitOrder(params: OrderSubmitParams) {
    return this.apiProvider.post(API_CONFIG.ORDER_SUBMIT, params).toPromise();
  }

  cancelOrder(orderId: string, params) {
    return this.apiProvider.post(API_CONFIG.ORDER_CANCEL + orderId, params).toPromise();
  }

  getTransactions() {
    return this.apiProvider.get(API_CONFIG.TRANSACTIONS).toPromise()
  }

}
