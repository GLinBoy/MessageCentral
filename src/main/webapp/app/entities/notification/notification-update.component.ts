import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import NotificationService from './notification.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type INotification, Notification } from '@/shared/model/notification.model';
import { MessageStatus } from '@/shared/model/enumerations/message-status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'NotificationUpdate',
  setup() {
    const notificationService = inject('notificationService', () => new NotificationService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const notification: Ref<INotification> = ref(new Notification());
    const messageStatusValues: Ref<string[]> = ref(Object.keys(MessageStatus));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveNotification = async notificationId => {
      try {
        const res = await notificationService().find(notificationId);
        res.createdAt = new Date(res.createdAt);
        notification.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.notificationId) {
      retrieveNotification(route.params.notificationId);
    }

    const initRelationships = () => {};

    initRelationships();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      username: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 64 }).toString(), 64),
      },
      token: {
        required: validations.required(t$('entity.validation.required').toString()),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 164 }).toString(), 164),
      },
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
      status: {},
      createdAt: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      createdBy: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      data: {},
    };
    const v$ = useVuelidate(validationRules, notification as any);
    v$.value.$validate();

    return {
      notificationService,
      alertService,
      notification,
      previousState,
      messageStatusValues,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: notification }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.notification.id) {
        this.notificationService()
          .update(this.notification)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('messageCentralApp.notification.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.notificationService()
          .create(this.notification)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('messageCentralApp.notification.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
