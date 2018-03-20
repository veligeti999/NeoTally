import { Component } from '@angular/core';
import {IonicPage} from "ionic-angular";

@IonicPage()
@Component({
  templateUrl: 'tabs.html'
})
export class TabsPage {

  tab1Root = 'OrderPage';
  tab2Root = 'TransactionsPage';
  tab3Root = 'ProfilePage';

  constructor() {

  }
}
