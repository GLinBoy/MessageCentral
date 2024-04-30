import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import NotificationDataService from './notification-data.service';
import { type INotificationData } from '@/shared/model/notification-data.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'NotificationDataDetails',
  setup() {
    const notificationDataService = inject('notificationDataService', () => new NotificationDataService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const notificationData: Ref<INotificationData> = ref({});

    const retrieveNotificationData = async notificationDataId => {
      try {
        const res = await notificationDataService().find(notificationDataId);
        notificationData.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.notificationDataId) {
      retrieveNotificationData(route.params.notificationDataId);
    }

    return {
      alertService,
      notificationData,

      previousState,
      t$: useI18n().t,
    };
  },
});
