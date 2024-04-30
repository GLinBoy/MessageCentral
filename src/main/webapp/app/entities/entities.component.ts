import { defineComponent, provide } from 'vue';

import EmailService from './email/email.service';
import ShortMessageService from './short-message/short-message.service';
import NotificationService from './notification/notification.service';
import NotificationDataService from './notification-data/notification-data.service';
import TokenService from './token/token.service';
import UserService from '@/entities/user/user.service';
// jhipster-needle-add-entity-service-to-entities-component-import - JHipster will import entities services here

export default defineComponent({
  compatConfig: { MODE: 3 },
  name: 'Entities',
  setup() {
    provide('userService', () => new UserService());
    provide('emailService', () => new EmailService());
    provide('shortMessageService', () => new ShortMessageService());
    provide('notificationService', () => new NotificationService());
    provide('notificationDataService', () => new NotificationDataService());
    provide('tokenService', () => new TokenService());
    // jhipster-needle-add-entity-service-to-entities-component - JHipster will import entities services here
  },
});
