import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ShortMessageService from './short-message.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IShortMessage, ShortMessage } from '@/shared/model/short-message.model';
import { MessageStatus } from '@/shared/model/enumerations/message-status.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ShortMessageUpdate',
  setup() {
    const shortMessageService = inject('shortMessageService', () => new ShortMessageService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const shortMessage: Ref<IShortMessage> = ref(new ShortMessage());
    const messageStatusValues: Ref<string[]> = ref(Object.keys(MessageStatus));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveShortMessage = async shortMessageId => {
      try {
        const res = await shortMessageService().find(shortMessageId);
        res.createdAt = new Date(res.createdAt);
        shortMessage.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.shortMessageId) {
      retrieveShortMessage(route.params.shortMessageId);
    }

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      phoneNumber: {
        required: validations.required(t$('entity.validation.required').toString()),
        minLength: validations.minLength(t$('entity.validation.minlength', { min: 7 }).toString(), 7),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 15 }).toString(), 15),
      },
      content: {
        required: validations.required(t$('entity.validation.required').toString()),
        minLength: validations.minLength(t$('entity.validation.minlength', { min: 6 }).toString(), 6),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 160 }).toString(), 160),
      },
      status: {},
      createdAt: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      createdBy: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
    };
    const v$ = useVuelidate(validationRules, shortMessage as any);
    v$.value.$validate();

    return {
      shortMessageService,
      alertService,
      shortMessage,
      previousState,
      messageStatusValues,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: shortMessage }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.shortMessage.id) {
        this.shortMessageService()
          .update(this.shortMessage)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('messageCentralApp.shortMessage.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.shortMessageService()
          .create(this.shortMessage)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('messageCentralApp.shortMessage.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
