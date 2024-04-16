/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import dayjs from 'dayjs';
import ShortMessageUpdate from './short-message-update.vue';
import ShortMessageService from './short-message.service';
import { DATE_TIME_LONG_FORMAT } from '@/shared/composables/date-format';
import AlertService from '@/shared/alert/alert.service';

type ShortMessageUpdateComponentType = InstanceType<typeof ShortMessageUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const shortMessageSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<ShortMessageUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('ShortMessage Management Update Component', () => {
    let comp: ShortMessageUpdateComponentType;
    let shortMessageServiceStub: SinonStubbedInstance<ShortMessageService>;

    beforeEach(() => {
      route = {};
      shortMessageServiceStub = sinon.createStubInstance<ShortMessageService>(ShortMessageService);
      shortMessageServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          shortMessageService: () => shortMessageServiceStub,
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('load', () => {
      beforeEach(() => {
        const wrapper = shallowMount(ShortMessageUpdate, { global: mountOptions });
        comp = wrapper.vm;
      });
      it('Should convert date from string', () => {
        // GIVEN
        const date = new Date('2019-10-15T11:42:02Z');

        // WHEN
        const convertedDate = comp.convertDateTimeFromServer(date);

        // THEN
        expect(convertedDate).toEqual(dayjs(date).format(DATE_TIME_LONG_FORMAT));
      });

      it('Should not convert date if date is not present', () => {
        expect(comp.convertDateTimeFromServer(null)).toBeNull();
      });
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(ShortMessageUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.shortMessage = shortMessageSample;
        shortMessageServiceStub.update.resolves(shortMessageSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(shortMessageServiceStub.update.calledWith(shortMessageSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        shortMessageServiceStub.create.resolves(entity);
        const wrapper = shallowMount(ShortMessageUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.shortMessage = entity;

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
        shortMessageServiceStub.find.resolves(shortMessageSample);
        shortMessageServiceStub.retrieve.resolves([shortMessageSample]);

        // WHEN
        route = {
          params: {
            shortMessageId: '' + shortMessageSample.id,
          },
        };
        const wrapper = shallowMount(ShortMessageUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.shortMessage).toMatchObject(shortMessageSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        shortMessageServiceStub.find.resolves(shortMessageSample);
        const wrapper = shallowMount(ShortMessageUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
