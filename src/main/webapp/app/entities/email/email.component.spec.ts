/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import Email from './email.vue';
import EmailService from './email.service';
import AlertService from '@/shared/alert/alert.service';

type EmailComponentType = InstanceType<typeof Email>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('Email Management Component', () => {
    let emailServiceStub: SinonStubbedInstance<EmailService>;
    let mountOptions: MountingOptions<EmailComponentType>['global'];

    beforeEach(() => {
      emailServiceStub = sinon.createStubInstance<EmailService>(EmailService);
      emailServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          jhiItemCount: true,
          bPagination: true,
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'jhi-sort-indicator': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          emailService: () => emailServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        emailServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(Email, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(emailServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.emails[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for an id', async () => {
        // WHEN
        const wrapper = shallowMount(Email, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(emailServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['id,asc'],
        });
      });
    });
    describe('Handles', () => {
      let comp: EmailComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(Email, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        emailServiceStub.retrieve.reset();
        emailServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('should load a page', async () => {
        // GIVEN
        emailServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.page = 2;
        await comp.$nextTick();

        // THEN
        expect(emailServiceStub.retrieve.called).toBeTruthy();
        expect(comp.emails[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should not load a page if the page is the same as the previous page', () => {
        // WHEN
        comp.page = 1;

        // THEN
        expect(emailServiceStub.retrieve.called).toBeFalsy();
      });

      it('should re-initialize the page', async () => {
        // GIVEN
        comp.page = 2;
        await comp.$nextTick();
        emailServiceStub.retrieve.reset();
        emailServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        comp.clear();
        await comp.$nextTick();

        // THEN
        expect(comp.page).toEqual(1);
        expect(emailServiceStub.retrieve.callCount).toEqual(1);
        expect(comp.emails[0]).toEqual(expect.objectContaining({ id: 123 }));
      });

      it('should calculate the sort attribute for a non-id attribute', async () => {
        // WHEN
        comp.propOrder = 'name';
        await comp.$nextTick();

        // THEN
        expect(emailServiceStub.retrieve.lastCall.firstArg).toMatchObject({
          sort: ['name,asc', 'id'],
        });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        emailServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeEmail();
        await comp.$nextTick(); // clear components

        // THEN
        expect(emailServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(emailServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
