import axios from 'axios';

const baseApiUrl = 'api/dashboard';

export default class DashboardService {
  public retrieveLast30DaysMessagesStatistic(): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .get(baseApiUrl)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }
}
