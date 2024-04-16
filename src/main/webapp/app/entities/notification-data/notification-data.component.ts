import { defineComponent, inject, onMounted, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';

import NotificationDataService from './notification-data.service';
import { type INotificationData } from '@/shared/model/notification-data.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'NotificationData',
  setup() {
    const { t: t$ } = useI18n();
    const notificationDataService = inject('notificationDataService', () => new NotificationDataService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const notificationData: Ref<INotificationData[]> = ref([]);

    const isFetching = ref(false);

    const clear = () => {};

    const retrieveNotificationDatas = async () => {
      isFetching.value = true;
      try {
        const res = await notificationDataService().retrieve();
        notificationData.value = res.data;
      } catch (err) {
        alertService.showHttpError(err.response);
      } finally {
        isFetching.value = false;
      }
    };

    const handleSyncList = () => {
      retrieveNotificationDatas();
    };

    onMounted(async () => {
      await retrieveNotificationDatas();
    });

    const removeId: Ref<number> = ref(null);
    const removeEntity = ref<any>(null);
    const prepareRemove = (instance: INotificationData) => {
      removeId.value = instance.id;
      removeEntity.value.show();
    };
    const closeDialog = () => {
      removeEntity.value.hide();
    };
    const removeNotificationData = async () => {
      try {
        await notificationDataService().delete(removeId.value);
        const message = t$('messageCentralApp.notificationData.deleted', { param: removeId.value }).toString();
        alertService.showInfo(message, { variant: 'danger' });
        removeId.value = null;
        retrieveNotificationDatas();
        closeDialog();
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    return {
      notificationData,
      handleSyncList,
      isFetching,
      retrieveNotificationDatas,
      clear,
      removeId,
      removeEntity,
      prepareRemove,
      closeDialog,
      removeNotificationData,
      t$,
    };
  },
});
