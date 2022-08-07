import Component from 'vue-class-component';
import { Inject, Vue } from 'vue-property-decorator';

@Component
export default class Dashboard extends Vue {
  @Inject('dashboardService') private dashboardService: () => DashboardService;

  public get authenticated(): boolean {
    return this.$store.getters.authenticated;
  }

  public get username(): string {
    return this.$store.getters.account ? this.$store.getters.account.login : '';
  }
}
