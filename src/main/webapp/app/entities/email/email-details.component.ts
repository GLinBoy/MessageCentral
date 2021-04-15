import { Component, Inject } from 'vue-property-decorator';

import { mixins } from 'vue-class-component';
import JhiDataUtils from '@/shared/data/data-utils.service';

import { IEmail } from '@/shared/model/email.model';
import EmailService from './email.service';

@Component
export default class EmailDetails extends mixins(JhiDataUtils) {
  @Inject('emailService') private emailService: () => EmailService;
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
      });
  }

  public previousState() {
    this.$router.go(-1);
  }
}
