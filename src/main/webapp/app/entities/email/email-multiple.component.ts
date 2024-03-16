import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import EmailService from './email.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { Emails, type IEmails } from '@/shared/model/email.model';
import { EmailType } from '@/shared/model/enumerations/email-type.model';

import Vue3TagsInput from 'vue3-tags-input';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'EmailMultiple',
  components: {
    Vue3TagsInput,
  },
  setup() {
    const emailService = inject('emailService', () => new EmailService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const emails: Ref<IEmails> = ref(new Emails());
    emails.value.emailType = EmailType.TEXT;
    emails.value.receivers = [];
    const emailTypeValues: Ref<string[]> = ref(Object.keys(EmailType));
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const dataUtils = useDataUtils();

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      receivers: {},
      subject: {
        required: validations.required(t$('entity.validation.required').toString()),
        minLength: validations.minLength(t$('entity.validation.minlength', { min: 4 }).toString(), 4),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 128 }).toString(), 128),
      },
      content: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
      status: {},
      emailType: {
        required: validations.required(t$('entity.validation.required').toString()),
      },
    };
    const v$ = useVuelidate(validationRules, emails as any);
    v$.value.$validate();

    const emailValidator = (email: string) => {
      const re =
        /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
      return re.test(String(email).toLowerCase());
    };

    const handleChangeTag = (tags: string[]) => {
      emails.value.receivers = tags;
    };

    return {
      emailService,
      alertService,
      emails,
      previousState,
      emailTypeValues,
      isSaving,
      currentLanguage,
      ...dataUtils,
      v$,
      ...useDateFormat({ entityRef: emails }),
      t$,
      emailValidator,
      handleChangeTag,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      this.emailService()
        .createMultiple([this.emails])
        .then(() => {
          this.isSaving = false;
          this.previousState();
          this.alertService.showSuccess(this.t$('messageCentralApp.email.createdMultiple'));
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService.showHttpError(error.response);
        });
    },
  },
});
