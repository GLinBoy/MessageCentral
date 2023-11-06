/* tslint:disable max-line-length */
import { shallowMount, createLocalVue, Wrapper } from '@vue/test-utils';
import sinon, { SinonStubbedInstance } from 'sinon';
import VueRouter from 'vue-router';

import * as config from '@/shared/config/config';
import TokenDetailComponent from '@/entities/token/token-details.vue';
import TokenClass from '@/entities/token/token-details.component';
import TokenService from '@/entities/token/token.service';
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
  describe('Token Management Detail Component', () => {
    let wrapper: Wrapper<TokenClass>;
    let comp: TokenClass;
    let tokenServiceStub: SinonStubbedInstance<TokenService>;

    beforeEach(() => {
      tokenServiceStub = sinon.createStubInstance<TokenService>(TokenService);

      wrapper = shallowMount<TokenClass>(TokenDetailComponent, {
        store,
        i18n,
        localVue,
        router,
        provide: { tokenService: () => tokenServiceStub, alertService: () => new AlertService() },
      });
      comp = wrapper.vm;
    });

    describe('OnInit', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        const foundToken = { id: 123 };
        tokenServiceStub.find.resolves(foundToken);

        // WHEN
        comp.retrieveToken(123);
        await comp.$nextTick();

        // THEN
        expect(comp.token).toBe(foundToken);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        const foundToken = { id: 123 };
        tokenServiceStub.find.resolves(foundToken);

        // WHEN
        comp.beforeRouteEnter({ params: { tokenId: 123 } }, null, cb => cb(comp));
        await comp.$nextTick();

        // THEN
        expect(comp.token).toBe(foundToken);
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
