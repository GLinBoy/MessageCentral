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

describe('ShortMessage e2e test', () => {
  const shortMessagePageUrl = '/short-message';
  const shortMessagePageUrlPattern = new RegExp('/short-message(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const shortMessageSample = {
    phoneNumber: '+55●63●59●27594',
    content: 'confirm',
    createdAt: '2024-12-27T15:24:21.345Z',
    createdBy: 'um boom pasta',
  };

  let shortMessage;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/short-messages+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/short-messages').as('postEntityRequest');
    cy.intercept('DELETE', '/api/short-messages/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (shortMessage) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/short-messages/${shortMessage.id}`,
      }).then(() => {
        shortMessage = undefined;
      });
    }
  });

  it('ShortMessages menu should load ShortMessages page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('short-message');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('ShortMessage').should('exist');
    cy.url().should('match', shortMessagePageUrlPattern);
  });

  describe('ShortMessage page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(shortMessagePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create ShortMessage page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/short-message/new$'));
        cy.getEntityCreateUpdateHeading('ShortMessage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shortMessagePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/short-messages',
          body: shortMessageSample,
        }).then(({ body }) => {
          shortMessage = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/short-messages+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/short-messages?page=0&size=20>; rel="last",<http://localhost/api/short-messages?page=0&size=20>; rel="first"',
              },
              body: [shortMessage],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(shortMessagePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details ShortMessage page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('shortMessage');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shortMessagePageUrlPattern);
      });

      it('edit button click should load edit ShortMessage page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ShortMessage');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shortMessagePageUrlPattern);
      });

      it('edit button click should load edit ShortMessage page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('ShortMessage');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shortMessagePageUrlPattern);
      });

      it('last delete button click should delete instance of ShortMessage', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('shortMessage').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', shortMessagePageUrlPattern);

        shortMessage = undefined;
      });
    });
  });

  describe('new ShortMessage page', () => {
    beforeEach(() => {
      cy.visit(`${shortMessagePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('ShortMessage');
    });

    it('should create an instance of ShortMessage', () => {
      cy.get(`[data-cy="phoneNumber"]`).type('+62●4●93●36');
      cy.get(`[data-cy="phoneNumber"]`).should('have.value', '+62●4●93●36');

      cy.get(`[data-cy="content"]`).type('tooXXX');
      cy.get(`[data-cy="content"]`).should('have.value', 'tooXXX');

      cy.get(`[data-cy="status"]`).select('IN_QUEUE');

      cy.get(`[data-cy="createdAt"]`).type('2024-12-28T02:51');
      cy.get(`[data-cy="createdAt"]`).blur();
      cy.get(`[data-cy="createdAt"]`).should('have.value', '2024-12-28T02:51');

      cy.get(`[data-cy="createdBy"]`).type('across');
      cy.get(`[data-cy="createdBy"]`).should('have.value', 'across');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        shortMessage = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', shortMessagePageUrlPattern);
    });
  });
});
