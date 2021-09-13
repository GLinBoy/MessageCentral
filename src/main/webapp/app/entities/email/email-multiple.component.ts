import { Component, Inject } from 'vue-property-decorator';

import { mixins } from 'vue-class-component';
import JhiDataUtils from '@/shared/data/data-utils.service';

import { required, minLength, maxLength } from 'vuelidate/lib/validators';

import { IEmails, Emails } from '@/shared/model/email.model';
import EmailService from './email.service';

const validations: any = {
  emails: {
    receivers: {
      required,
    },
    subject: {
      required,
      minLength: minLength(4),
      maxLength: maxLength(128),
    },
    content: {
      required,
    },
    status: {},
  },
};

@Component({
  validations,
})
export default class EmailUpdate extends mixins(JhiDataUtils) {
  @Inject('emailService') private emailService: () => EmailService;
  public emails: IEmails = new Emails();
  public isSaving = false;
  public currentLanguage = '';

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
    this.emailService()
      .createMultiple([this.emails])
      .then(() => {
        this.isSaving = false;
        this.$router.go(-1);
        const message = this.$t('messageCentralApp.email.created', -1);
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

  public emailValidator(email) {
    const re =
      /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
    return re.test(String(email).toLowerCase());
  }
}
