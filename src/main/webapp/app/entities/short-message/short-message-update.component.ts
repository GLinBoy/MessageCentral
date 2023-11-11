import { Component, Vue, Inject } from 'vue-property-decorator';

import { required, minLength, maxLength } from 'vuelidate/lib/validators';
import dayjs from 'dayjs';
import { DATE_TIME_LONG_FORMAT } from '@/shared/date/filters';

import AlertService from '@/shared/alert/alert.service';

import { IShortMessage, ShortMessage } from '@/shared/model/short-message.model';
import ShortMessageService from './short-message.service';
import { MessageStatus } from '@/shared/model/enumerations/message-status.model';

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
export default class ShortMessageUpdate extends Vue {
  @Inject('shortMessageService') private shortMessageService: () => ShortMessageService;
  @Inject('alertService') private alertService: () => AlertService;

  public shortMessage: IShortMessage = new ShortMessage();
  public messageStatusValues: string[] = Object.keys(MessageStatus);
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
      this.shortMessageService()
        .create(this.shortMessage)
        .then(param => {
          this.isSaving = false;
          this.$router.go(-1);
          const message = this.$t('messageCentralApp.shortMessage.created', { param: param.id });
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
      this.shortMessage[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.shortMessage[field] = null;
    }
  }

  public updateZonedDateTimeField(field, event) {
    if (event.target.value) {
      this.shortMessage[field] = dayjs(event.target.value, DATE_TIME_LONG_FORMAT);
    } else {
      this.shortMessage[field] = null;
    }
  }

  public retrieveShortMessage(shortMessageId): void {
    this.shortMessageService()
      .find(shortMessageId)
      .then(res => {
        res.createdAt = new Date(res.createdAt);
        this.shortMessage = res;
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
