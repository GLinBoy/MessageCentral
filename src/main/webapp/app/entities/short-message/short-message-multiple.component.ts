import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, minLength, maxLength } from 'vuelidate/lib/validators';

import AlertService from '@/shared/alert/alert.service';

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
  @Inject('alertService') private alertService: () => AlertService;

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
      .then(() => {
        this.isSaving = false;
        this.$router.go(-1);
        const message = this.$t('messageCentralApp.shortMessage.createdMultiple');
        return (this.$root as any).$bvToast.toast(message.toString(), {
          toaster: 'b-toaster-top-center',
          title: 'Success',
          variant: 'success',
          solid: true,
          autoHideDelay: 5000,
        });
      })
      .catch(error => {
        this.isSaving = false;
        this.alertService().showHttpError(this, error.response);
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
