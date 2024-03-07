import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';

import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';
import DashboardService from './dashboard.service';

import { Bar } from 'vue-chartjs';
import { BarElement, CategoryScale, Chart as ChartJS, Legend, LinearScale, Title, Tooltip } from 'chart.js';

import { type IMessagesStatistics } from '@/shared/model/messages-statistics.model';
import { MessageType } from '@/shared/model/enumerations/message-type.model';
import { ChartData, type IChartData } from '@/shared/model/chart-data.model';
import { Dataset } from '@/shared/model/dataset.model';
import { ChartOptions, type IChartOptions } from '@/shared/model/chart-options.model';

ChartJS.register(Title, Tooltip, Legend, BarElement, CategoryScale, LinearScale);

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Dashboard',
  components: {
    Bar,
  },
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
      default: 150,
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
  setup(props, ctx) {
    const dateFormat = useDateFormat();
    const dashboardService = inject('dashboardService', () => new DashboardService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const messagesStatistics: Ref<IMessagesStatistics[]> = ref([]);

    const isFetching: Ref<boolean> = ref(false);

    const messageTypeKeys: Ref<string[]> = ref(Object.keys(MessageType));

    const chartDataSelected: Ref<MessageType> = ref(MessageType.ALL);

    const failedLabel: Ref<string> = ref(useI18n().t('dashboard.chart.failed'));
    const successfulLabel: Ref<string> = ref(useI18n().t('dashboard.chart.successful'));
    const failedColor: Ref<string> = ref('#C70039');
    const successfulColor: Ref<string> = ref('#669933');

    const chartData: Ref<IChartData> = ref(
      new ChartData([], [new Dataset(failedLabel, failedColor, []), new Dataset(successfulLabel, successfulColor, [])]),
    );

    const chartOptions: Ref<IChartOptions> = ref(new ChartOptions());

    const retrieveLast30DaysMessagesStatistic = async () => {
      isFetching.value = true;
      try {
        const res = await dashboardService().retrieveLast30DaysMessagesStatistic();
        messagesStatistics.value = res.data;
        loadChartDataOfAllMessages();
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const loadChartDataOfAllMessages = () => {
      const dates: Array<string> = messagesStatistics.value.map(item => formatDateOfChart(item.date.toString()));
      const successfulArray: Array<number> = messagesStatistics.value.map(item => {
        return item.email.successful + item.sms.successful + item.notification.successful;
      });
      const failedArray: Array<number> = messagesStatistics.value.map(item => {
        return item.email.failed + item.sms.failed + item.notification.failed;
      });
      updateChartData(dates, successfulArray, failedArray);
    };

    const loadChartDataOfEmails = () => {
      const dates: Array<string> = messagesStatistics.value.map(item => formatDateOfChart(item.date.toString()));
      const successfulArray: Array<number> = messagesStatistics.value.map(item => item.email.successful);
      const failedArray: Array<number> = messagesStatistics.value.map(item => item.email.failed);
      updateChartData(dates, successfulArray, failedArray);
    };

    const loadChartDataOfNotifications = () => {
      const dates: Array<string> = messagesStatistics.value.map(item => formatDateOfChart(item.date.toString()));
      const successfulArray: Array<number> = messagesStatistics.value.map(item => item.notification.successful);
      const failedArray: Array<number> = messagesStatistics.value.map(item => item.notification.failed);
      updateChartData(dates, successfulArray, failedArray);
    };

    const loadChartDataOfSms = () => {
      const dates: Array<string> = messagesStatistics.value.map(item => formatDateOfChart(item.date.toString()));
      const successfulArray: Array<number> = messagesStatistics.value.map(item => item.sms.successful);
      const failedArray: Array<number> = messagesStatistics.value.map(item => item.sms.failed);
      updateChartData(dates, successfulArray, failedArray);
    };

    const formatDateOfChart = (dateStr: string): string => {
      const d = new Date(dateStr);
      return `${d.toLocaleString('default', { day: 'numeric', month: 'short' })}`;
    };

    const updateChartData = (data: Array<string>, successfulCount: Array<number>, failedCount: Array<number>) => {
      chartData.value = new ChartData(data, [
        new Dataset(failedLabel.value, failedColor.value, failedCount),
        new Dataset(successfulLabel.value, successfulColor.value, successfulCount),
      ]);
    };

    const entityMap: Map<MessageType, Function> = new Map([
      [MessageType.ALL, loadChartDataOfAllMessages],
      [MessageType.EMAIL, loadChartDataOfEmails],
      [MessageType.SMS, loadChartDataOfSms],
      [MessageType.NOTIFICATION, loadChartDataOfNotifications],
    ]);

    const loadChartData = (type: MessageType): void => {
      isFetching.value = true;
      if (entityMap.has(type)) {
        entityMap.get(type).apply(null);
        chartDataSelected.value = type;
      } else {
        console.error(`the "${type}" type doesn't exist!`);
        loadChartDataOfAllMessages();
        chartDataSelected.value = MessageType.ALL;
      }
      isFetching.value = false;
    };

    onMounted(async () => {
      await retrieveLast30DaysMessagesStatistic();
    });

    return {
      ...dateFormat,
      alertService,

      ...dataUtils,

      t$: useI18n().t,

      isFetching,
      messagesStatistics,
      messageTypeKeys,
      chartDataSelected,
      failedLabel,
      successfulLabel,
      failedColor,
      successfulColor,
      chartData,
      chartOptions,
      loadChartData,
    };
  },
});
