import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, minLength, maxLength } from 'vuelidate/lib/validators';

import { IShortMessages, ShortMessages } from '@/shared/model/short-message.model';
import ShortMessageService from './short-message.service';

const validations: any = {
  shortMessages: {
    phoneNumbers: {
      required,
    },
    content: {
      required,
      minLength: minLength(6),
      maxLength: maxLength(160),
    },
    status: {},
  },
};

@Component({
  validations,
})
export default class ShortMessageMultiple extends Vue {
  @Inject('shortMessageService') private shortMessageService: () => ShortMessageService;
  public shortMessages: IShortMessages = new ShortMessages();
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.shortMessageId) {
        vm.retrieveShortMessage(to.params.shortMessageId);
      }
    });
  }

  created(): void {
    this.currentLanguage = this.$store.getters.currentLanguage;
    this.$store.watch(
      () => this.$store.getters.currentLanguage,
      () => {
        this.currentLanguage = this.$store.getters.currentLanguage;
      }
    );
  }

  public save(): void {
    this.isSaving = true;
    this.shortMessageService()
      .createMultiple([this.shortMessages])
      .then(param => {
        this.isSaving = false;
        this.$router.go(-1);
        const message = this.$t('messageCentralApp.shortMessage.created', { param: param.id });
        this.$root.$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Success',
          variant: 'success',
          solid: true,
          autoHideDelay: 5000,
        });
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {}

  public numberValidator(number) {
    return true;
  }
}
