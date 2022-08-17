import { mixins } from 'vue-class-component';
import { Component, Inject } from 'vue-property-decorator';
import { IMessagesStatistics } from '@/shared/model/messages-statistics.model';

import DashboardService from './dashboard.service';
import JhiDataUtils from '@/shared/data/data-utils.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class Dashboard extends mixins(JhiDataUtils) {
  @Inject('dashboardService') private dashboardService: () => DashboardService;
  @Inject('alertService') private alertService: () => AlertService;

  public messagesStatistics: IMessagesStatistics[] = [];

  public isFetching = false;

  public mounted(): void {
    this.retrieveLast30DaysMessagesStatistic();
  }

  public retrieveLast30DaysMessagesStatistic(): void {
    this.isFetching = true;
    this.dashboardService()
      .retrieveLast30DaysMessagesStatistic()
      .then(
        res => {
          this.messagesStatistics = res.data;
          console.log(this.messagesStatistics);
          this.isFetching = false;
        },
        err => {
          this.isFetching = false;
          console.error(err);
          this.alertService().showHttpError(this, err.response);
        }
      );
  }
}
