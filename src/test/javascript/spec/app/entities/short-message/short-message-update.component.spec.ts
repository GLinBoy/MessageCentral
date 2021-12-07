/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import Router from 'vue-router';
import { ToastPlugin } from 'bootstrap-vue';

import * as config from '@/shared/config/config';
import ShortMessageUpdateComponent from '@/entities/short-message/short-message-update.vue';
import ShortMessageClass from '@/entities/short-message/short-message-update.component';
import ShortMessageService from '@/entities/short-message/short-message.service';

import AlertService from '@/shared/alert/alert.service';

const localVue = createLocalVue();

config.initVueApp(localVue);
const i18n = config.initI18N(localVue);
const store = config.initVueXStore(localVue);
const router = new Router();
localVue.use(Router);
localVue.use(ToastPlugin);
localVue.component('font-awesome-icon', {});
localVue.component('b-input-group', {});
localVue.component('b-input-group-prepend', {});
localVue.component('b-form-datepicker', {});
localVue.component('b-form-input', {});

describe('Component Tests', () => {
  describe('ShortMessage Management Update Component', () => {
    let wrapper: Wrapper<ShortMessageClass>;
    let comp: ShortMessageClass;
    let shortMessageServiceStub: SinonStubbedInstance<ShortMessageService>;

    beforeEach(() => {
      shortMessageServiceStub = sinon.createStubInstance<ShortMessageService>(ShortMessageService);

      wrapper = shallowMount<ShortMessageClass>(ShortMessageUpdateComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: {
          shortMessageService: () => shortMessageServiceStub,
          alertService: () => new AlertService(),
        },
      });
      comp = wrapper.vm;
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const entity = { id: 123 };
        comp.shortMessage = entity;
        shortMessageServiceStub.update.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(shortMessageServiceStub.update.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        comp.shortMessage = entity;
        shortMessageServiceStub.create.resolves(entity);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(shortMessageServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundShortMessage = { id: 123 };
        shortMessageServiceStub.find.resolves(foundShortMessage);
        shortMessageServiceStub.retrieve.resolves([foundShortMessage]);

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
