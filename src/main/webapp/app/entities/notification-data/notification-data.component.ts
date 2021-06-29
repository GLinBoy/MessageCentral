import { mixins } from 'vue-class-component';

import { Component, Vue, Inject } from 'vue-property-decorator';
import Vue2Filters from 'vue2-filters';
import { INotificationData } from '@/shared/model/notification-data.model';

import NotificationDataService from './notification-data.service';

@Component({
  mixins: [Vue2Filters.mixin],
})
export default class NotificationData extends Vue {
  @Inject('notificationDataService') private notificationDataService: () => NotificationDataService;
  private removeId: number = null;

  public notificationData: INotificationData[] = [];

  public isFetching = false;

  public mounted(): void {
    this.retrieveAllNotificationDatas();
  }

  public clear(): void {
    this.retrieveAllNotificationDatas();
  }

  public retrieveAllNotificationDatas(): void {
    this.isFetching = true;

    this.notificationDataService()
      .retrieve()
      .then(
        res => {
          this.notificationData = res.data;
          this.isFetching = false;
        },
        err => {
          this.isFetching = false;
        }
      );
  }

  public handleSyncList(): void {
    this.clear();
  }

  public prepareRemove(instance: INotificationData): void {
    this.removeId = instance.id;
    if (<any>this.$refs.removeEntity) {
      (<any>this.$refs.removeEntity).show();
    }
  }

  public removeNotificationData(): void {
    this.notificationDataService()
      .delete(this.removeId)
      .then(() => {
        const message = this.$t('messageCentralApp.notificationData.deleted', { param: this.removeId });
        this.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Info',
          variant: 'danger',
          solid: true,
          autoHideDelay: 5000,
        });
        this.removeId = null;
        this.retrieveAllNotificationDatas();
        this.closeDialog();
      });
  }

  public closeDialog(): void {
    (<any>this.$refs.removeEntity).hide();
  }
}
