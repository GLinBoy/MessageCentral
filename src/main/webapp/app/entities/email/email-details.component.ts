import { defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';

import EmailService from './email.service';
import useDataUtils from '@/shared/data/data-utils.service';
import { useDateFormat } from '@/shared/composables';
import { type IEmail } from '@/shared/model/email.model';
import { useAlertService } from '@/shared/alert/alert.service';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'EmailDetails',
  setup() {
    const dateFormat = useDateFormat();
    const emailService = inject('emailService', () => new EmailService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const dataUtils = useDataUtils();

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);
    const email: Ref<IEmail> = ref({});

    const retrieveEmail = async emailId => {
      try {
        const res = await emailService().find(emailId);
        email.value = res;
      } catch (error) {
        alertService.showHttpError(error.response);
      }
    };

    if (route.params?.emailId) {
      retrieveEmail(route.params.emailId);
    }

    return {
      ...dateFormat,
      alertService,
      email,

      ...dataUtils,

      previousState,
      t$: useI18n().t,
    };
  },
});
