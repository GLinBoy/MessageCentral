import { computed, defineComponent, inject, ref, type Ref } from 'vue';
import { useI18n } from 'vue-i18n';
import { useRoute, useRouter } from 'vue-router';
import { useVuelidate } from '@vuelidate/core';

import ShortMessageService from './short-message.service';
import { useDateFormat, useValidation } from '@/shared/composables';
import { useAlertService } from '@/shared/alert/alert.service';

import { type IShortMessages, ShortMessages } from '@/shared/model/short-message.model';

import Vue3TagsInput from 'vue3-tags-input';

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'ShortMessageMultiple',
  components: {
    Vue3TagsInput,
  },
  setup() {
    const shortMessageService = inject('shortMessageService', () => new ShortMessageService());
    const alertService = inject('alertService', () => useAlertService(), true);

    const shortMessages: Ref<IShortMessages> = ref(new ShortMessages());
    const isSaving = ref(false);
    const currentLanguage = inject('currentLanguage', () => computed(() => navigator.language ?? 'en'), true);

    const route = useRoute();
    const router = useRouter();

    const previousState = () => router.go(-1);

    const { t: t$ } = useI18n();
    const validations = useValidation();
    const validationRules = {
      phoneNumbers: {},
      content: {
        required: validations.required(t$('entity.validation.required').toString()),
        minLength: validations.minLength(t$('entity.validation.minlength', { min: 6 }).toString(), 6),
        maxLength: validations.maxLength(t$('entity.validation.maxlength', { max: 160 }).toString(), 160),
      },
      status: {},
    };
    const v$ = useVuelidate(validationRules, shortMessages as any);
    v$.value.$validate();

    const handleChangeTag = (tags: string[]) => {
      shortMessages.value.phoneNumbers = tags;
    };

    return {
      shortMessageService,
      alertService,
      shortMessages,
      previousState,
      isSaving,
      currentLanguage,
      v$,
      ...useDateFormat({ entityRef: shortMessages }),
      t$,
      handleChangeTag,
    };
  },
  created(): void {},
  methods: {
    save(): void {
      this.isSaving = true;
      this.shortMessageService()
        .createMultiple([this.shortMessages])
        .then(() => {
          this.isSaving = false;
          this.previousState();
          this.alertService.showInfo(this.t$('messageCentralApp.shortMessage.createdMultiple'));
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService.showHttpError(error.response);
        });
    },
  },
});
