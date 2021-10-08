import Vue from 'vue';
import { Component, Inject } from 'vue-property-decorator';
import UserManagementService from './user-management.service';

@Component
export default class JhiUserManagementView extends Vue {
  @Inject('userService') private userManagementService: () => UserManagementService;
  public user: any = null;

  beforeRouteEnter(to, from, next) {
    next(vm => {
      if (to.params.userId) {
        vm.init(to.params.userId);
      }
    });
  }
  public init(userId: number): void {
    this.userManagementService()
      .get(userId)
      .then(res => {
        this.user = res.data;
      });
  }
}
