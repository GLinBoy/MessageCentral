import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import EmailService from './email.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useValidation, useDateFormat } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IEmail, Email } from '@/shared/model/email.model';
import { MessageStatus } from '@/shared/model/enumerations/message-status.model';
import { EmailType } from '@/shared/model/enumerations/email-type.model';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'EmailUpdate',
  setup() {
    const emailService = inject('emailService', () => new EmailService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const email: Ref<IEmail> = ref(new Email());
    const messageStatusValues: Ref<string[]> = ref(Object.keys(MessageStatus));
    const emailTypeValues: Ref<string[]> = ref(Object.keys(EmailType));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const retrieveEmail = async emailId => {
      try {
        const res = await emailService().find(emailId);
        res.createdAt = new Date(res.createdAt);
        email.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.emailId) {
      retrieveEmail(route.params.emailId);
    }

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      receiver: {
        required: validations.required(t$('entity.validation.required').toString()),
        minLength: validations.minLength(t$('entity.validation.minlength', { min: 8 }).toString(), 8),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 128 }).toString(), 128),
      },
      subject: {
        required: validations.required(t$('entity.validation.required').toString()),
        minLength: validations.minLength(t$('entity.validation.minlength', { min: 4 }).toString(), 4),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 128 }).toString(), 128),
      },
      content: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      status: {},
      emailType: {},
      createdAt: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      createdBy: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
    };
    const v$ = useVuelidate(validationRules, email as any);
    v$.value.$validate();

    return {
      emailService,
      alertService,
      email,
      previousState,
      messageStatusValues,
      emailTypeValues,
      isSaving,
      currentLanguage,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: email }),
      t$,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      if (this.email.id) {
        this.emailService()
          .update(this.email)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showInfo(this.t$('messageCentralApp.email.updated', { param: param.id }));
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      } else {
        this.emailService()
          .create(this.email)
          .then(param => {
            this.isSaving = false;
            this.previousState();
            this.alertService.showSuccess(this.t$('messageCentralApp.email.created', { param: param.id }).toString());
          })
          .catch(error => {
            this.isSaving = false;
            this.alertService.showHttpError(error.response);
          });
      }
    },
  },
});
