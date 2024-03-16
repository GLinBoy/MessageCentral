/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { type MountingOptions, shallowMount } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import NotificationData from './notification-data.vue';
import NotificationDataService from './notification-data.service';
import AlertService from '@/shared/alert/alert.service';

type NotificationDataComponentType = InstanceType<typeof NotificationData>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('NotificationData Management Component', () => {
    let notificationDataServiceStub: SinonStubbedInstance<NotificationDataService>;
    let mountOptions: MountingOptions<NotificationDataComponentType>['global'];

    beforeEach(() => {
      notificationDataServiceStub = sinon.createStubInstance<NotificationDataService>(NotificationDataService);
      notificationDataServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          notificationDataService: () => notificationDataServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        notificationDataServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(NotificationData, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(notificationDataServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.notificationData[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: NotificationDataComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(NotificationData, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        notificationDataServiceStub.retrieve.reset();
        notificationDataServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        notificationDataServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeNotificationData();
        await comp.$nextTick(); // clear components

        // THEN
        expect(notificationDataServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(notificationDataServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
