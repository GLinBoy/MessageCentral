/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import EmailDetailComponent from '@/entities/email/email-details.vue';
import EmailClass from '@/entities/email/email-details.component';
import EmailService from '@/entities/email/email.service';
import router from '@/router';
import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('Email Management Detail Component', () => {
    let wrapper: Wrapper<EmailClass>;
    let comp: EmailClass;
    let emailServiceStub: SinonStubbedInstance<EmailService>;

    beforeEach(() => {
      emailServiceStub = sinon.createStubInstance<EmailService>(EmailService);

      wrapper = shallowMount<EmailClass>(EmailDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { emailService: () => emailServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundEmail = { id: 123 };
        emailServiceStub.find.resolves(foundEmail);

        // WHEN
        comp.retrieveEmail(123);
        await comp.$nextTick();

        // THEN
        expect(comp.email).toBe(foundEmail);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundEmail = { id: 123 };
        emailServiceStub.find.resolves(foundEmail);

        // WHEN
        comp.beforeRouteEnter({ params: { emailId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.email).toBe(foundEmail);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        comp.previousState();
        await comp.$nextTick();

        expect(comp.$router.currentRoute.fullPath).toContain('/');
      });
    });
  });
});
