/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import ShortMessageDetailComponent from '@/entities/short-message/short-message-details.vue';
import ShortMessageClass from '@/entities/short-message/short-message-details.component';
import ShortMessageService from '@/entities/short-message/short-message.service';
import router from '@/router';

const localVue = createLocalVue();
localVue.use(VueRouter);

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
localVue.component('font-awesome-icon', {});
localVue.component('router-link', {});

describe('Component Tests', () => {
  describe('ShortMessage Management Detail Component', () => {
    let wrapper: Wrapper<ShortMessageClass>;
    let comp: ShortMessageClass;
    let shortMessageServiceStub: SinonStubbedInstance<ShortMessageService>;

    beforeEach(() => {
      shortMessageServiceStub = sinon.createStubInstance<ShortMessageService>(ShortMessageService);

      wrapper = shallowMount<ShortMessageClass>(ShortMessageDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { shortMessageService: () => shortMessageServiceStub },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundShortMessage = { id: 123 };
        shortMessageServiceStub.find.resolves(foundShortMessage);

        // WHEN
        comp.retrieveShortMessage(123);
        await comp.$nextTick();

        // THEN
        expect(comp.shortMessage).toBe(foundShortMessage);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundShortMessage = { id: 123 };
        shortMessageServiceStub.find.resolves(foundShortMessage);

        // WHEN
        comp.beforeRouteEnter({ params: { shortMessageId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.shortMessage).toBe(foundShortMessage);
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
