import { mixins } from 'vue-class-component';
import { Component, Inject } from 'vue-property-decorator';
import { IMessagesStatistics } from '@/shared/model/messages-statistics.model';
import { ChartData, IChartData } from '@/shared/model/chart-data.model';
import { ChartOptions, IChartOptions } from '@/shared/model/chart-options.model';
import { Dataset } from '@/shared/model/dataset.model';
import { MessageType } from '@/shared/model/enumerations/message-type.model';
import { Bar } from 'vue-chartjs/legacy';
import { BarElement, CategoryScale, Chart as ChartJS, Legend, LinearScale, Title, Tooltip } from 'chart.js';
import DashboardService from './dashboard.service';
import JhiDataUtils from '@/shared/data/data-utils.service';
import AlertService from '@/shared/alert/alert.service';

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale);

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
    notificationsSuccessfulSent(): number {
      return this.messagesStatistics.reduce((sum, current) => sum + current.notification.successful, 0);
    },
    notificationsFailedSent(): number {
      return this.messagesStatistics.reduce((sum, current) => sum + current.notification.failed, 0);
    },
    totalSuccessfulSent(): number {
      return this.smsSuccessfulSent + this.emailsSuccessfulSent + this.notificationsSuccessfulSent;
    },
    totalFailedSent(): number {
      return this.smsFailedSent + this.emailsFailedSent + this.notificationsFailedSent;
    },
  },
  components: {
    Bar,
  },
  props: {
    chartId: {
      type: String,
      default: 'bar-chart',
    },
    datasetIdKey: {
      type: String,
      default: 'label',
    },
    width: {
      type: Number,
      default: 400,
    },
    height: {
      type: Number,
      default: 400,
    },
    cssClasses: {
      default: '',
      type: String,
    },
    styles: {
      type: Object,
      default: () => {},
    },
    plugins: {
      type: Object,
      default: () => {},
    },
  },
})
export default class Dashboard extends mixins(JhiDataUtils) {
  @Inject('dashboardService') private dashboardService: () => DashboardService;
  @Inject('alertService') private alertService: () => AlertService;

  public messagesStatistics: IMessagesStatistics[] = [];

  public isFetching = false;

  public chartDataSelected: MessageType = MessageType.ALL;

  failedLabel = 'failed';
  successfulLabel = 'successful';
  failedColor = '#ff6d71';
  successfulColor = '#370018';

  public chartData: IChartData = new ChartData(
    [],
    [new Dataset(this.failedLabel, this.failedColor, []), new Dataset(this.successfulLabel, this.successfulColor, [])]
  );

  public chartOptions: IChartOptions = new ChartOptions(true);

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
          this.loadChartDataOfAllMessages();
          this.isFetching = false;
        },
        err => {
          this.isFetching = false;
          console.error(err);
          this.alertService().showHttpError(this, err.response);
        }
      );
  }

  loadChartDataOfAllMessages() {
    const dates: Array<string> = this.messagesStatistics.map(item => {
      const d = new Date(item.date);
      return `${d.toLocaleString('default', { day: 'numeric', month: 'short' })}`;
    });
    const successfulCount: Array<number> = this.messagesStatistics.map(item => {
      return item.email.successful + item.sms.successful + item.notification.successful;
    });
    const failedCounter: Array<number> = this.messagesStatistics.map(item => {
      return item.email.failed + item.sms.failed + item.notification.failed;
    });
    this.chartData = new ChartData(dates, [
      new Dataset(this.failedLabel, this.failedColor, failedCounter),
      new Dataset(this.successfulLabel, this.successfulColor, successfulCount),
    ]);
  }

  loadChartDataOfEmails() {
    const dates: Array<string> = this.messagesStatistics.map(item => {
      const d = new Date(item.date);
      return `${d.toLocaleString('default', { day: 'numeric', month: 'short' })}`;
    });
    const successfulEmail: Array<number> = this.messagesStatistics.map(item => item.email.successful);
    const failedEmail: Array<number> = this.messagesStatistics.map(item => item.email.failed);
    this.chartData = new ChartData(dates, [
      new Dataset(this.failedLabel, this.failedColor, failedEmail),
      new Dataset(this.successfulLabel, this.successfulColor, successfulEmail),
    ]);
  }

  loadChartDataOfNotifications() {
    const dates: Array<string> = this.messagesStatistics.map(item => {
      const d = new Date(item.date);
      return `${d.toLocaleString('default', { day: 'numeric', month: 'short' })}`;
    });
    const successfulNotification: Array<number> = this.messagesStatistics.map(item => item.notification.successful);
    const failedNotification: Array<number> = this.messagesStatistics.map(item => item.notification.failed);
    this.chartData = new ChartData(dates, [
      new Dataset(this.failedLabel, this.failedColor, failedNotification),
      new Dataset(this.successfulLabel, this.successfulColor, successfulNotification),
    ]);
  }

  loadChartDataOfSms() {
    const dates: Array<string> = this.messagesStatistics.map(item => {
      const d = new Date(item.date);
      return `${d.toLocaleString('default', { day: 'numeric', month: 'short' })}`;
    });
    const successfulSms: Array<number> = this.messagesStatistics.map(item => item.sms.successful);
    const failedSms: Array<number> = this.messagesStatistics.map(item => item.sms.failed);
    this.chartData = new ChartData(dates, [
      new Dataset(this.failedLabel, this.failedColor, failedSms),
      new Dataset(this.successfulLabel, this.successfulColor, successfulSms),
    ]);
  }

  public loadChartData(type: MessageType): void {
    this.isFetching = true;
    switch (type) {
      case MessageType.EMAIL:
        this.loadChartDataOfEmails();
        break;
      case MessageType.NOTIFICATION:
        this.loadChartDataOfNotifications();
        break;
      case MessageType.SMS:
        this.loadChartDataOfSms();
        break;
      default:
        this.loadChartDataOfAllMessages();
        break;
    }
    this.chartDataSelected = type;
    this.isFetching = false;
  }
}
