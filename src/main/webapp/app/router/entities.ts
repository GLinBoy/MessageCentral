import { Authority } from '@/shared/security/authority';
/* tslint:disable */
// prettier-ignore

// prettier-ignore
const Email = () => import('@/entities/email/email.vue');
// prettier-ignore
const EmailUpdate = () => import('@/entities/email/email-update.vue');
// prettier-ignore
const EmailDetails = () => import('@/entities/email/email-details.vue');
// jhipster-needle-add-entity-to-router-import - JHipster will import entities to the router here

export default [
  {
    path: '/email',
    name: 'Email',
    component: Email,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/email/new',
    name: 'EmailCreate',
    component: EmailUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/email/:emailId/edit',
    name: 'EmailEdit',
    component: EmailUpdate,
    meta: { authorities: [Authority.USER] },
  },
  {
    path: '/email/:emailId/view',
    name: 'EmailView',
    component: EmailDetails,
    meta: { authorities: [Authority.USER] },
  },
  // jhipster-needle-add-entity-to-router - JHipster will add entities to the router here
];
