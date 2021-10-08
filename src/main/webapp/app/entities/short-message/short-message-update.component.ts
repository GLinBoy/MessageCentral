import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, minLength, maxLength } from 'vuelidate/lib/validators';

import { IShortMessage, ShortMessage } from '@/shared/model/short-message.model';
import ShortMessageService from './short-message.service';

const validations: any = {
  shortMessage: {
    phoneNumber: {
      required,
      minLength: minLength(7),
      maxLength: maxLength(15),
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
export default class ShortMessageUpdate extends Vue {
  @Inject('shortMessageService') private shortMessageService: () => ShortMessageService;
  public shortMessage: IShortMessage = new ShortMessage();
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
    if (this.shortMessage.id) {
      this.shortMessageService()
        .update(this.shortMessage)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('messageCentralApp.shortMessage.updated', { param: param.id });
          return this.$root.$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        });
    } else {
      this.shortMessageService()
        .create(this.shortMessage)
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
  }

  public retrieveShortMessage(shortMessageId): void {
    this.shortMessageService()
      .find(shortMessageId)
      .then(res => {
        this.shortMessage = res;
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {}
}
