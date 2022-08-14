import { mixins } from 'vue-class-component';
import { Component, Inject } from 'vue-property-decorator';

import DashboardService from './dashboard.service';
import JhiDataUtils from '@/shared/data/data-utils.service';

@Component
export default class Dashboard extends mixins(JhiDataUtils) {
  @Inject('dashboardService') private dashboardService: () => DashboardService;

  public mounted(): void {
    this.retrieveLast30DaysMessagesStatistic();
  }

  public retrieveLast30DaysMessagesStatistic(): void {
    this.dashboardService()
      .retrieveLast30DaysMessagesStatistic()
      .then(
        res => {
          console.log(res);
        },
        err => {
          console.error(err);
        }
      );
  }
}
