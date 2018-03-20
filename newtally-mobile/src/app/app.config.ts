import {PushOptions} from "@ionic-native/push";
export enum TransactionStatus {
  COMPLETED = 'completed',
  PENDING = 'pending',
  FAILED = 'failed'
}

export const API_CONFIG = {
  DOMAIN: 'http://18.219.39.174:8080/',
  COUNTER_ENDPOINT: 'http://18.219.39.174:8080/new-tally/rest/counters/',
  // COUNTER_ENDPOINT: 'http://localhost:8100/new-tally/rest/counters/',
  DISCOUNT: 'currency/discounts/',
  QR_CODE: 'currency/qrCode/',
  ORDER_CANCEL: 'order/cancel/',
  ORDER_SUBMIT: 'order/submit/',
  TRANSACTIONS: 'transactions/',
  DEVICE: 'device/'
};

export const PUSH_CONFIG: PushOptions = {
  android: {
    sound: "true",
    forceShow: "true",
    senderID: "729360866832"
  },
  ios: {
    alert: "true",
    badge: "true",
    sound: "true"
  },
}

export enum NetworkError {

}
