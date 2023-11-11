import { Component, Inject } from 'vue-property-decorator';

import { mixins } from 'vue-class-component';
import JhiDataUtils from '@/shared/data/data-utils.service';

import { required, minLength, maxLength } from 'vuelidate/lib/validators';
import dayjs from 'dayjs';
import { DATE_TIME_LONG_FORMAT } from '@/shared/date/filters';

import AlertService from '@/shared/alert/alert.service';

import { IEmail, Email } from '@/shared/model/email.model';
import EmailService from './email.service';
import { MessageStatus } from '@/shared/model/enumerations/message-status.model';
import { EmailType } from '@/shared/model/enumerations/email-type.model';

const validations: any = {
  email: {
    receiver: {
      required,
      minLength: minLength(8),
      maxLength: maxLength(128),
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
    emailType: {},
    createdAt: {
      required,
    },
    createdBy: {
      required,
    },
  },
};

@Component({
  validations,
})
export default class EmailUpdate extends mixins(JhiDataUtils) {
  @Inject('emailService') private emailService: () => EmailService;
  @Inject('alertService') private alertService: () => AlertService;

  public email: IEmail = new Email();
  public messageStatusValues: string[] = Object.keys(MessageStatus);
  public emailTypeValues: string[] = Object.keys(EmailType);
  public isSaving = false;
  public currentLanguage = '';

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.emailId) {
        vm.retrieveEmail(to.params.emailId);
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
    if (this.email.id) {
      this.emailService()
        .update(this.email)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('messageCentralApp.email.updated', { param: param.id });
          return (this.$root as any).$bvToast.toast(message.toString(), {
            toaster: 'b-toaster-top-center',
            title: 'Info',
            variant: 'info',
            solid: true,
            autoHideDelay: 5000,
          });
        })
        .catch(error => {
          this.isSaving = false;
          this.alertService().showHttpError(this, error.response);
        });
    } else {
      this.emailService()
        .create(this.email)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('messageCentralApp.email.created', { param: param.id });
          (this.$root as any).$bvToast.toast(message.toString(), {
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
  }

  public convertDateTimeFromServer(date: Date): string {
    if (date && dayjs(date).isValid()) {
      return dayjs(date).format(DATE_TIME_LONG_FORMAT);
    }
    return null;
  }

  public updateInstantField(field, event) {
    if (event.target.value) {
      this.email[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.email[field] = null;
    }
  }

  public updateZonedDateTimeField(field, event) {
    if (event.target.value) {
      this.email[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.email[field] = null;
    }
  }

  public retrieveEmail(emailId): void {
    this.emailService()
      .find(emailId)
      .then(res => {
        res.createdAt = new Date(res.createdAt);
        this.email = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState(): void {
    this.$router.go(-1);
  }

  public initRelationships(): void {}
}
