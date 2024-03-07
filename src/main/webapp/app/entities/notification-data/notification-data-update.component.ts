import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import NotificationDataService from './notification-data.service';
import { useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import NotificationService from '@/entities/notification/notification.service';
import { type INotification } from '@/shared/model/notification.model';
import { type INotificationData, NotificationData } from '@/shared/model/notification-data.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'NotificationDataUpdate',
  setup() {
    const notificationDataService = inject('notificationDataService', () => new NotificationDataService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const notificationData: Ref<INotificationData> = ref(new NotificationData());

    const notificationService = inject('notificationService', () => new NotificationService());

    const notifications: Ref<INotification[]> = ref([]);
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

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

    const initRelationships = () => {
      notificationService()
        .retrieve()
        .then(res => {
          notifications.value = res.data;
        });
    };

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      dataKey: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 128 }).toString(), 128),
      },
      dataValue: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 256 }).toString(), 256),
      },
      notification: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
    };
    const v$ = useVuelidate(validationRules, notificationData as any);
    v$.value.$validate();

    return {
      notificationDataService,
      alertService,
      notificationData,
      previousState,
      isSaving,
      currentLanguage,
      notifications,
      v$,
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.notificationData.id) {
        this.notificationDataService()
          .update(this.notificationData)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('messageCentralApp.notificationData.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.notificationDataService()
          .create(this.notificationData)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('messageCentralApp.notificationData.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
