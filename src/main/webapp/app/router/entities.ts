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
const ShortMessage = () => import('@/entities/short-message/short-message.vue');
// prettier-ignore
const ShortMessageUpdate = () => import('@/entities/short-message/short-message-update.vue');
// prettier-ignore
const ShortMessageDetails = () => import('@/entities/short-message/short-message-details.vue');
// prettier-ignore
const Notification = () => import('@/entities/notification/notification.vue');
// prettier-ignore
const NotificationUpdate = () => import('@/entities/notification/notification-update.vue');
// prettier-ignore
const NotificationDetails = () => import('@/entities/notification/notification-details.vue');
// prettier-ignore
const NotificationData = () => import('@/entities/notification-data/notification-data.vue');
// prettier-ignore
const NotificationDataUpdate = () => import('@/entities/notification-data/notification-data-update.vue');
// prettier-ignore
const NotificationDataDetails = () => import('@/entities/notification-data/notification-data-details.vue');
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
    component: EmailUpdate,
    meta: { authorities: [Authority.ADMIN, Authority.EMAIL_USER] },
  },
  {
    path: '/email/:emailId/edit',
    name: 'EmailEdit',
    component: EmailUpdate,
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
    component: ShortMessageUpdate,
    meta: { authorities: [Authority.ADMIN, Authority.SMS_USER] },
  },
  {
    path: '/short-message/:shortMessageId/edit',
    name: 'ShortMessageEdit',
    component: ShortMessageUpdate,
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
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/notification/new',
    name: 'NotificationCreate',
    component: NotificationUpdate,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/notification/:notificationId/edit',
    name: 'NotificationEdit',
    component: NotificationUpdate,
    meta: { authorities: [Authority.ADMIN] },
  },
  {
    path: '/notification/:notificationId/view',
    name: 'NotificationView',
    component: NotificationDetails,
    meta: { authorities: [Authority.ADMIN] },
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
  // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
];
