import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore

// prettier-ignore
const Email = () => import('@/entities/email/email.vue');
// prettier-ignore
const EmailUpdate = () => import('@/entities/email/email-update.vue');
// prettier-ignore
const EmailDetails = () => import('@/entities/email/email-details.vue');
// prettier-ignore
const EmailMultiple = () => import('@/entities/email/email-multiple.vue');
// prettier-ignore
const ShortMessage = () => import('@/entities/short-message/short-message.vue');
// prettier-ignore
const ShortMessageUpdate = () => import('@/entities/short-message/short-message-update.vue');
// prettier-ignore
const ShortMessageDetails = () => import('@/entities/short-message/short-message-details.vue');
// prettier-ignore
const ShortMessageMultiple = () => import('@/entities/short-message/short-message-multiple.vue');
// prettier-ignore
const Notification = () => import('@/entities/notification/notification.vue');
// prettier-ignore
const NotificationUpdate = () => import('@/entities/notification/notification-update.vue');
// prettier-ignore
const NotificationDetails = () => import('@/entities/notification/notification-details.vue');
// prettier-ignore
const NotificationMultiple = () => import('@/entities/notification/notification-multiple.vue');
// prettier-ignore
const NotificationData = () => import('@/entities/notification-data/notification-data.vue');
// prettier-ignore
const NotificationDataUpdate = () => import('@/entities/notification-data/notification-data-update.vue');
// prettier-ignore
const NotificationDataDetails = () => import('@/entities/notification-data/notification-data-details.vue');
// prettier-ignore
const Token = () => import('@/entities/token/token.vue');
// prettier-ignore
const TokenUpdate = () => import('@/entities/token/token-update.vue');
// prettier-ignore
const TokenDetails = () => import('@/entities/token/token-details.vue');
// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default [
  {
    path: '/email',
    name: 'Email',
    component: Email,
    meta: { authorities: [Authority.ADMIN, Authority.EMAIL_USER] },
  },
  {
    path: '/email/new',
    name: 'EmailCreate',
    component: EmailMultiple,
    meta: { authorities: [Authority.ADMIN, Authority.EMAIL_USER] },
  },
  {
    path: '/email/:emailId/view',
    name: 'EmailView',
    component: EmailDetails,
    meta: { authorities: [Authority.ADMIN, Authority.EMAIL_USER] },
  },
  {
    path: '/short-message',
    name: 'ShortMessage',
    component: ShortMessage,
    meta: { authorities: [Authority.ADMIN, Authority.SMS_USER] },
  },
  {
    path: '/short-message/new',
    name: 'ShortMessageCreate',
    component: ShortMessageMultiple,
    meta: { authorities: [Authority.ADMIN, Authority.SMS_USER] },
  },
  {
    path: '/short-message/:shortMessageId/view',
    name: 'ShortMessageView',
    component: ShortMessageDetails,
    meta: { authorities: [Authority.ADMIN, Authority.SMS_USER] },
  },
  {
    path: '/notification',
    name: 'Notification',
    component: Notification,
    meta: { authorities: [Authority.ADMIN, Authority.NOTIFICATION_USER] },
  },
  {
    path: '/notification/new',
    name: 'NotificationCreate',
    component: NotificationMultiple,
    meta: { authorities: [Authority.ADMIN, Authority.NOTIFICATION_USER] },
  },
  {
    path: '/notification/:notificationId/view',
    name: 'NotificationView',
    component: NotificationDetails,
    meta: { authorities: [Authority.ADMIN, Authority.NOTIFICATION_USER] },
  },
  {
    path: '/notification-data',
    name: 'NotificationData',
    component: NotificationData,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/notification-data/new',
    name: 'NotificationDataCreate',
    component: NotificationDataUpdate,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/notification-data/:notificationDataId/edit',
    name: 'NotificationDataEdit',
    component: NotificationDataUpdate,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/notification-data/:notificationDataId/view',
    name: 'NotificationDataView',
    component: NotificationDataDetails,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/token',
    name: 'Token',
    component: Token,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/token/new',
    name: 'TokenCreate',
    component: TokenUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/token/:tokenId/edit',
    name: 'TokenEdit',
    component: TokenUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/token/:tokenId/view',
    name: 'TokenView',
    component: TokenDetails,
    meta: { authorities: [Authority.USER] },
  },
  // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
];
