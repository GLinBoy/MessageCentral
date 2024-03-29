import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore
const Entities = () => import('@/entities/entities.vue');

const Email = () => import('@/entities/email/email.vue');
const EmailUpdate = () => import('@/entities/email/email-update.vue');
const EmailDetails = () => import('@/entities/email/email-details.vue');
const EmailMultiple = () => import('@/entities/email/email-multiple.vue');

const ShortMessage = () => import('@/entities/short-message/short-message.vue');
const ShortMessageUpdate = () => import('@/entities/short-message/short-message-update.vue');
const ShortMessageDetails = () => import('@/entities/short-message/short-message-details.vue');
const ShortMessageMultiple = () => import('@/entities/short-message/short-message-multiple.vue');

const Notification = () => import('@/entities/notification/notification.vue');
const NotificationUpdate = () => import('@/entities/notification/notification-update.vue');
const NotificationDetails = () => import('@/entities/notification/notification-details.vue');
const NotificationMultiple = () => import('@/entities/notification/notification-multiple.vue');

const NotificationData = () => import('@/entities/notification-data/notification-data.vue');
const NotificationDataUpdate = () => import('@/entities/notification-data/notification-data-update.vue');
const NotificationDataDetails = () => import('@/entities/notification-data/notification-data-details.vue');

const Token = () => import('@/entities/token/token.vue');
const TokenUpdate = () => import('@/entities/token/token-update.vue');
const TokenDetails = () => import('@/entities/token/token-details.vue');

// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default {
  path: '/',
  component: Entities,
  children: [
    {
      path: 'email',
      name: 'Email',
      component: Email,
      meta: { authorities: [Authority.USER, Authority.EMAIL_USER] },
    },
    {
      path: 'email/new',
      name: 'EmailCreate',
      component: EmailMultiple,
      meta: { authorities: [Authority.USER, Authority.EMAIL_USER] },
    },
    {
      path: 'email/:emailId/edit',
      name: 'EmailEdit',
      component: EmailUpdate,
      meta: { authorities: [Authority.USER, Authority.EMAIL_USER] },
    },
    {
      path: 'email/:emailId/view',
      name: 'EmailView',
      component: EmailDetails,
      meta: { authorities: [Authority.USER, Authority.EMAIL_USER] },
    },
    {
      path: 'short-message',
      name: 'ShortMessage',
      component: ShortMessage,
      meta: { authorities: [Authority.USER, Authority.SMS_USER] },
    },
    {
      path: 'short-message/new',
      name: 'ShortMessageCreate',
      component: ShortMessageMultiple,
      meta: { authorities: [Authority.USER, Authority.SMS_USER] },
    },
    {
      path: 'short-message/:shortMessageId/edit',
      name: 'ShortMessageEdit',
      component: ShortMessageUpdate,
      meta: { authorities: [Authority.USER, Authority.SMS_USER] },
    },
    {
      path: 'short-message/:shortMessageId/view',
      name: 'ShortMessageView',
      component: ShortMessageDetails,
      meta: { authorities: [Authority.USER, Authority.SMS_USER] },
    },
    {
      path: 'notification',
      name: 'Notification',
      component: Notification,
      meta: { authorities: [Authority.USER, Authority.NOTIFICATION_USER] },
    },
    {
      path: 'notification/new',
      name: 'NotificationCreate',
      component: NotificationMultiple,
      meta: { authorities: [Authority.USER, Authority.NOTIFICATION_USER] },
    },
    {
      path: 'notification/:notificationId/edit',
      name: 'NotificationEdit',
      component: NotificationUpdate,
      meta: { authorities: [Authority.USER, Authority.NOTIFICATION_USER] },
    },
    {
      path: 'notification/:notificationId/view',
      name: 'NotificationView',
      component: NotificationDetails,
      meta: { authorities: [Authority.USER, Authority.NOTIFICATION_USER] },
    },
    {
      path: 'notification-data',
      name: 'NotificationData',
      component: NotificationData,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'notification-data/new',
      name: 'NotificationDataCreate',
      component: NotificationDataUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'notification-data/:notificationDataId/edit',
      name: 'NotificationDataEdit',
      component: NotificationDataUpdate,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'notification-data/:notificationDataId/view',
      name: 'NotificationDataView',
      component: NotificationDataDetails,
      meta: { authorities: [Authority.USER] },
    },
    {
      path: 'admin/token',
      name: 'Token',
      component: Token,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'admin/token/new',
      name: 'TokenCreate',
      component: TokenUpdate,
      meta: { authorities: [Authority.ADMIN] },
    },
    {
      path: 'admin/token/:tokenId/view',
      name: 'TokenView',
      component: TokenDetails,
      meta: { authorities: [Authority.ADMIN] },
    },
    // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
  ],
};
