/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';

import * as config from '@/shared/config/config';
import NotificationDataComponent from '@/entities/notification-data/notification-data.vue';
import NotificationDataClass from '@/entities/notification-data/notification-data.component';
import NotificationDataService from '@/entities/notification-data/notification-data.service';

const localVue = createLocalVue();

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('b-badge', {});
localVue.directive('b-modal', {});
localVue.component('b-button', {});
localVue.component('router-link', {});

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  describe('NotificationData Management Component', () => {
    let wrapper: Wrapper<NotificationDataClass>;
    let comp: NotificationDataClass;
    let notificationDataServiceStub: SinonStubbedInstance<NotificationDataService>;

    beforeEach(() => {
      notificationDataServiceStub = sinon.createStubInstance<NotificationDataService>(NotificationDataService);
      notificationDataServiceStub.retrieve.resolves({ headers: {} });

      wrapper = shallowMount<NotificationDataClass>(NotificationDataComponent, {
        store,
        i18n,
        localVue,
        stubs: { bModal: bModalStub as any },
        provide: {
          notificationDataService: () => notificationDataServiceStub,
        },
      });
      comp = wrapper.vm;
    });

    it('Should call load all on init', async () => {
      // GIVEN
      notificationDataServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

      // WHEN
      comp.retrieveAllNotificationDatas();
      await comp.$nextTick();

      // THEN
      expect(notificationDataServiceStub.retrieve.called).toBeTruthy();
      expect(comp.notificationData[0]).toEqual(expect.objectContaining({ id: 123 }));
    });
    it('Should call delete service on confirmDelete', async () => {
      // GIVEN
      notificationDataServiceStub.delete.resolves({});

      // WHEN
      comp.prepareRemove({ id: 123 });
      comp.removeNotificationData();
      await comp.$nextTick();

      // THEN
      expect(notificationDataServiceStub.delete.called).toBeTruthy();
      expect(notificationDataServiceStub.retrieve.callCount).toEqual(1);
    });
  });
});
