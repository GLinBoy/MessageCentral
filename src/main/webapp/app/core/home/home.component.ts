import { type ComputedRef, defineComponent, inject } from 'vue';
import { useI18n } from 'vue-i18n';

import { useLoginModal } from '@/account/login-modal';

import LoginForm from '@/account/login-form/login-form.vue';

export default defineComponent({
  compatConfig: { MODE: 3 },
  components: {
    'login-form': LoginForm,
  },
  setup() {
    const { showLogin } = useLoginModal();
    const authenticated = inject<ComputedRef<boolean>>('authenticated');
    const username = inject<ComputedRef<string>>('currentUsername');

    return {
      authenticated,
      username,
      showLogin,
      t$: useI18n().t,
    };
  },
});
