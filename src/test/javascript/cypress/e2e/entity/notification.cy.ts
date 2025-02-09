import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('Notification e2e test', () => {
  const notificationPageUrl = '/notification';
  const notificationPageUrlPattern = new RegExp('/notification(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const notificationSample = {
    username: 'energetically vengeful ugh',
    token: 'likewise incomparable',
    subject: 'um athwart viciously',
    content: 'moral',
    createdAt: '2024-12-28T04:04:25.511Z',
    createdBy: 'yowza phooey upbeat',
  };

  let notification;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/notifications+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/notifications').as('postEntityRequest');
    cy.intercept('DELETE', '/api/notifications/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (notification) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/notifications/${notification.id}`,
      }).then(() => {
        notification = undefined;
      });
    }
  });

  it('Notifications menu should load Notifications page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('notification');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Notification').should('exist');
    cy.url().should('match', notificationPageUrlPattern);
  });

  describe('Notification page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(notificationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Notification page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/notification/new$'));
        cy.getEntityCreateUpdateHeading('Notification');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', notificationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/notifications',
          body: notificationSample,
        }).then(({ body }) => {
          notification = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/notifications+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/notifications?page=0&size=20>; rel="last",<http://localhost/api/notifications?page=0&size=20>; rel="first"',
              },
              body: [notification],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(notificationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Notification page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('notification');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', notificationPageUrlPattern);
      });

      it('edit button click should load edit Notification page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Notification');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', notificationPageUrlPattern);
      });

      it('edit button click should load edit Notification page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Notification');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', notificationPageUrlPattern);
      });

      it('last delete button click should delete instance of Notification', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('notification').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', notificationPageUrlPattern);

        notification = undefined;
      });
    });
  });

  describe('new Notification page', () => {
    beforeEach(() => {
      cy.visit(`${notificationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Notification');
    });

    it('should create an instance of Notification', () => {
      cy.get(`[data-cy="username"]`).type('whereas');
      cy.get(`[data-cy="username"]`).should('have.value', 'whereas');

      cy.get(`[data-cy="token"]`).type('naturally leading');
      cy.get(`[data-cy="token"]`).should('have.value', 'naturally leading');

      cy.get(`[data-cy="subject"]`).type('circular');
      cy.get(`[data-cy="subject"]`).should('have.value', 'circular');

      cy.get(`[data-cy="content"]`).type('why');
      cy.get(`[data-cy="content"]`).should('have.value', 'why');

      cy.get(`[data-cy="image"]`).type('enthusiastically cuddly usually');
      cy.get(`[data-cy="image"]`).should('have.value', 'enthusiastically cuddly usually');

      cy.get(`[data-cy="status"]`).select('SENT');

      cy.get(`[data-cy="createdAt"]`).type('2024-12-27T12:14');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2024-12-27T12:14');

      cy.get(`[data-cy="createdBy"]`).type('though for bloom');
      cy.get(`[data-cy="createdBy"]`).should('have.value', 'though for bloom');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        notification = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', notificationPageUrlPattern);
    });
  });
});
