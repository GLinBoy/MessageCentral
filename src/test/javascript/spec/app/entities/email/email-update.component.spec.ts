/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';

import * as config from '@/shared/config/config';
import EmailUpdateComponent from '@/entities/email/email-update.vue';
import EmailClass from '@/entities/email/email-update.component';
import EmailService from '@/entities/email/email.service';

import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
const router = new Router();
localVue.use(Router);
localVue.component('font-awesome-icon', {});
localVue.component('b-input-group', {});
localVue.component('b-input-group-prepend', {});
localVue.component('b-form-datepicker', {});
localVue.component('b-form-input', {});

describe('Component Tests', () => {
  describe('Email Management Update Component', () => {
    let wrapper: Wrapper<EmailClass>;
    let comp: EmailClass;
    let emailServiceStub: SinonStubbedInstance<EmailService>;

    beforeEach(() => {
      emailServiceStub = sinon.createStubInstance<EmailService>(EmailService);

      wrapper = shallowMount<EmailClass>(EmailUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          emailService: () => emailServiceStub,
          alertService: () => new AlertService(),
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.email = entity;
        emailServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(emailServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.email = entity;
        emailServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(emailServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundEmail = { id: 123 };
        emailServiceStub.find.resolves(foundEmail);
        emailServiceStub.retrieve.resolves([foundEmail]);

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
