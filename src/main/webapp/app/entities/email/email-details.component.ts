import { Component, Inject } from 'vue-property-decorator';

import { mixins } from 'vue-class-component';
import JhiDataUtils from '@/shared/data/data-utils.service';

import { IEmail } from '@/shared/model/email.model';
import { MessageStatus } from '@/shared/model/enumerations/message-status.model';
import EmailService from './email.service';
import AlertService from '@/shared/alert/alert.service';

@Component
export default class EmailDetails extends mixins(JhiDataUtils) {
  @Inject('emailService') private emailService: () => EmailService;
  @Inject('alertService') private alertService: () => AlertService;

  public email: IEmail = {};

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.emailId) {
        vm.retrieveEmail(to.params.emailId);
      }
    });
  }

  public retrieveEmail(emailId) {
    this.emailService()
      .find(emailId)
      .then(res => {
        this.email = res;
      })
      .catch(error => {
        this.alertService().showHttpError(this, error.response);
      });
  }

  public previousState() {
    this.$router.go(-1);
  }

  public getVariant(status) {
    if (MessageStatus.IN_QUEUE === status) {
      return 'info';
    } else if (MessageStatus.SENT === status) {
      return 'success';
    } else if (MessageStatus.FAILED === status) {
      return 'danger';
    } else {
      return 'secondary';
    }
  }
}
