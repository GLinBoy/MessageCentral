import axios from 'axios';

import buildPaginationQueryOpts from '@/shared/sort/sorts';
import buildSearchQueryOpts from '@/shared/search/searchs';

import { INotification, INotifications } from '@/shared/model/notification.model';

const baseApiUrl = 'api/notifications';

export default class NotificationService {
  public find(id: number): Promise<INotification> {
    return new Promise<INotification>((resolve, reject) => {
      axios
        .get(`${baseApiUrl}/${id}`)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public retrieve(paginationQuery?: any): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .get(baseApiUrl + `?${buildPaginationQueryOpts(paginationQuery)}` + `${buildSearchQueryOpts(paginationQuery)}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public delete(id: number): Promise<any> {
    return new Promise<any>((resolve, reject) => {
      axios
        .delete(`${baseApiUrl}/${id}`)
        .then(res => {
          resolve(res);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public create(entity: INotification): Promise<INotification> {
    return new Promise<INotification>((resolve, reject) => {
      axios
        .post(`${baseApiUrl}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public update(entity: INotification): Promise<INotification> {
    return new Promise<INotification>((resolve, reject) => {
      axios
        .put(`${baseApiUrl}/${entity.id}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  public partialUpdate(entity: INotification): Promise<INotification> {
    return new Promise<INotification>((resolve, reject) => {
      axios
        .patch(`${baseApiUrl}/${entity.id}`, entity)
        .then(res => {
          resolve(res.data);
        })
        .catch(err => {
          reject(err);
        });
    });
  }

  /**
   * Create multiple Notifications at once
   */
  public createMultiple(entity: INotifications[]): Promise {
    return new Promise((resolve, reject) => {
      axios
        .post(`${baseApiUrl}/multiple`, entity)
        .then(res => {
          resolve();
        })
        .catch(err => {
          reject(err);
        });
    });
  }
}
