import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import NotificationService from './notification.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type INotifications, type IReceiver, Notifications, Receiver } from '@/shared/model/notification.model';
import { type INotificationData, NotificationData } from '@/shared/model/notification-data.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'NotificationMultiple',
  setup() {
    const notificationService = inject('notificationService', () => new NotificationService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const notifications: Ref<INotifications> = ref(new Notifications());
    const receiver: Ref<IReceiver> = ref(new Receiver());
    const notificationData: Ref<INotificationData> = ref(new NotificationData());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const initRelationships = () => {};

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      // username: {
      //   required: validations.required(t$('entity.validation.required').toString()),
      //   maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 64 }).toString(), 64),
      // },
      // token: {
      //   required: validations.required(t$('entity.validation.required').toString()),
      //   maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 164 }).toString(), 164),
      // },
      subject: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 128 }).toString(), 128),
      },
      content: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 4000 }).toString(), 4000),
      },
      image: {
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 256 }).toString(), 256),
      },
      data: {},
    };
    const v$ = useVuelidate(validationRules, notifications as any);
    v$.value.$validate();

    const addNotificationData = () => {
      if (notifications.value.data) {
        notifications.value.data = notifications.value.data.filter(obj => obj.dataKey !== notificationData.value.dataKey);
        notifications.value.data.push(notificationData.value);
      } else {
        notifications.value.data = [notificationData.value];
      }
      console.log(notifications.value.data);
      resetNotificationData();
    };

    const resetNotificationData = () => {
      notificationData.value = new NotificationData();
    };

    const prepareNotificationDataEdit = (data: INotificationData) => {
      notificationData.value = data;
    };

    const prepareNotificationDataRemove = (data: INotificationData) => {
      notifications.value.data = notifications.value.data.filter(obj => obj.dataKey !== data.dataKey);
    };

    const addReceiver = () => {
      if (notifications.value.receivers) {
        notifications.value.receivers = notifications.value.receivers.filter(
          obj => obj.username !== receiver.value.username && obj.token !== receiver.value.token,
        );
        notifications.value.receivers.push(receiver.value);
      } else {
        notifications.value.receivers = [receiver.value];
      }
      resetReceiver();
    };

    const resetReceiver = () => {
      receiver.value = new Receiver();
    };

    const prepareReceiverEdit = (data: IReceiver) => {
      receiver.value = data;
    };

    const prepareReceiverRemove = (data: IReceiver) => {
      notifications.value.receivers = notifications.value.receivers.filter(
        obj => obj.username !== data.username && obj.token !== data.token,
      );
    };

    return {
      notificationService,
      alertService,
      notifications,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: notifications }),
      t$,
      receiver,
      notificationData,
      addNotificationData,
      resetNotificationData,
      prepareNotificationDataEdit,
      prepareNotificationDataRemove,
      addReceiver,
      resetReceiver,
      prepareReceiverEdit,
      prepareReceiverRemove,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      this.notificationService()
        .createMultiple([this.notifications])
        .then(() => {
          this.isSaving = false;
          this.previousState();
          this.alertService.showInfo(this.t$('messageCentralApp.notification.createdMultiple'));
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService.showHttpError(error.response);
        });
    },
  },
});
