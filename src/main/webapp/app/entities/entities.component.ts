import { Component, Provide, Vue } from 'vue-property-decorator';

import UserService from '@/entities/user/user.service';
import EmailService from './email/email.service';
import ShortMessageService from './short-message/short-message.service';
import NotificationService from './notification/notification.service';
import NotificationDataService from './notification-data/notification-data.service';
import TokenService from './token/token.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

@Component
export default class Entities extends Vue {
  @Provide('userService') private userService = () => new UserService();
  @Provide('emailService') private emailService = () => new EmailService();
  @Provide('shortMessageService') private shortMessageService = () => new ShortMessageService();
  @Provide('notificationService') private notificationService = () => new NotificationService();
  @Provide('notificationDataService') private notificationDataService = () => new NotificationDataService();
  @Provide('tokenService') private tokenService = () => new TokenService();
  // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
}
