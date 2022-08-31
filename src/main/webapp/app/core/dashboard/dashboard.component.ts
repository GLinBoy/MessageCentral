import { mixins } from 'vue-class-component';
import { Component, Inject } from 'vue-property-decorator';
import { IMessagesStatistics } from '@/shared/model/messages-statistics.model';

import DashboardService from './dashboard.service';
import JhiDataUtils from '@/shared/data/data-utils.service';
import AlertService from '@/shared/alert/alert.service';

@Component({
  computed: {
    smsSuccessfulSent(): number {
      return this.messagesStatistics.reduce((sum, current) => sum + current.sms.successful, 0);
    },
    smsFailedSent(): number {
      return this.messagesStatistics.reduce((sum, current) => sum + current.sms.failed, 0);
    },
    emailsSuccessfulSent(): number {
      return this.messagesStatistics.reduce((sum, current) => sum + current.email.successful, 0);
    },
    emailsFailedSent(): number {
      return this.messagesStatistics.reduce((sum, current) => sum + current.email.failed, 0);
    },
  },
})
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
