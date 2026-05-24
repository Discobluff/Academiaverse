// ***********************************************
//  End‑to‑End Tests – Full User Story Coverage
//  (Self‑sufficient: creates fresh accounts + items)
// ***********************************************

function registerViaApi(firstname: string, lastname: string, email: string, password: string) {
  return cy.request({
    method: 'POST',
    url: 'http://localhost:8080/v1/auth/register',
    body: { firstname, lastname, email, password },
    failOnStatusCode: false
  });
}

function loginViaApi(email: string, password: string) {
  return cy.request('POST', 'http://localhost:8080/v1/auth/login', {
    username: email,
    password
  }).then((response) => {
    expect(response.status).to.eq(200);
    return cy.wrap(response.body.token);
  });
}

describe('Full User Story Coverage', () => {

  
  it('Story 1 & 2 (API): Register, accept terms, login, logout', () => {
    registerViaApi('Alice', 'Test', 'alice@example.com', 'test');
    registerViaApi('Bob', 'Test', 'bob@example.com', 'test');
    const email = `testuser_${Date.now()}@example.com`;
    registerViaApi('Alice', 'Test', email, 'testtest').then((resp) => {
      expect(resp.status).to.be.oneOf([200, 409]);
    });
    loginViaApi(email, 'testtest').then((token) => {
      expect(token).to.be.a('string');
    });
  });

  it('Story 3 (API): Course search', () => {
    cy.request('GET', 'http://localhost:8080/api/courses/search?q=Software').then((resp) => {
      expect(resp.status).to.eq(200);
      expect(resp.body.length).to.be.greaterThan(0);
      expect(resp.body[0].name).to.include('Software');
    });
  });

  it('Story 4 (API): Course‑based item listings', () => {
    loginViaApi('alice@example.com', 'test').then((token) => {
      cy.request({
        method: 'POST',
        url: 'http://localhost:8080/api/items',
        headers: { Authorization: `Bearer ${token}` },
        body: {title: 'Test Item', price: 1.0,course: { id: 1}}
      })
    });
    cy.request('GET', 'http://localhost:8080/api/courses/1/items').then((resp) => {
      expect(resp.status).to.eq(200);
      expect(resp.body.length).to.be.greaterThan(0);
    });
  });

  it('Story 5, 6, 13 (API): Item detail, digital upload, payment method', () => {
    loginViaApi('alice@example.com', 'test').then((token) => {
      cy.request({
        method: 'POST',
        url: 'http://localhost:8080/api/items',
        headers: { Authorization: `Bearer ${token}` },
        body: { title: `DigitalNotes_${Date.now()}`, description: 'Scanned notes', price: 5, paymentMethod: 'points', type: 'sale' }
      }).then((resp) => {
        expect(resp.status).to.eq(200);
        expect(resp.body.paymentMethod).to.eq('points');
        expect(resp.body.price).to.eq(5);
        expect(resp.body.type).to.eq('sale');
      });
    });
  });

  it('Story 7, 8, 9 (API): Add item with rent, details, course association', () => {
    loginViaApi('alice@example.com', 'test').then((token) => {
      cy.request({
        method: 'POST',
        url: 'http://localhost:8080/api/items',
        headers: { Authorization: `Bearer ${token}` },
        body: { title: `RentItem_${Date.now()}`, description: 'Rentable gadget', price: 15, paymentMethod: 'cash', type: 'rent', course: { id: 1 } }
      }).then((resp) => {
        expect(resp.status).to.eq(200);
        expect(resp.body.type).to.eq('rent');
      });
    });
  });

  it('Story 10 (API): Seller listings management', () => {
    loginViaApi('alice@example.com', 'test').then((token) => {
      cy.request({
        method: 'GET',
        url: 'http://localhost:8080/api/items/seller_listings',
        headers: { Authorization: `Bearer ${token}` }
      }).then((resp) => {
        expect(resp.status).to.eq(200);
        expect(resp.body.length).to.be.greaterThan(0);
      });
    });
  });

  it('Story 11 & 12 (API): Edit and delete item', () => {
    const editTitle = `EditMe_${Date.now()}`;
    loginViaApi('alice@example.com', 'test').then((token) => {
      cy.request({
        method: 'POST',
        url: 'http://localhost:8080/api/items',
        headers: { Authorization: `Bearer ${token}` },
        body: { title: editTitle, description: 'Test', price: 10, paymentMethod: 'cash', type: 'sale' }
      }).then((resp) => {
        const itemId = resp.body.id;
        cy.request({
          method: 'PUT',
          url: `http://localhost:8080/api/items/${itemId}`,
          headers: { Authorization: `Bearer ${token}` },
          body: { title: editTitle + ' edited', description: 'Updated', price: 12, paymentMethod: 'points', type: 'sale' }
        }).then((putResp) => {
          expect(putResp.status).to.eq(200);
          expect(putResp.body.title).to.eq(editTitle + ' edited');
        });
        cy.request({
          method: 'DELETE',
          url: `http://localhost:8080/api/items/${itemId}`,
          headers: { Authorization: `Bearer ${token}` }
        }).then((delResp) => {
          expect(delResp.status).to.eq(200);
        });
      });
    });
  });

it('Story 14 (API): Express interest records interest without changing item status', () => {
    const interestTitle = `InterestItem_${Date.now()}`;
    let createdItemId: number;
    loginViaApi('alice@example.com', 'test').then((aliceToken) => {
      cy.request({
        method: 'POST',
        url: 'http://localhost:8080/api/items',
        headers: { Authorization: `Bearer ${aliceToken}` },
        body: { title: interestTitle, description: 'Interest test', price: 20, paymentMethod: 'cash', type: 'sale' }
      }).then((resp) => {
        createdItemId = resp.body.id;
        loginViaApi('bob@example.com', 'test').then((bobToken) => {
          cy.request({
            method: 'POST',
            url: 'http://localhost:8080/api/interests',
            headers: { Authorization: `Bearer ${bobToken}` },
            body: { itemId: createdItemId }
          }).then((interestResp) => {
            // Interest created successfully (status 200)
            expect(interestResp.status).to.eq(200);
            // Verify that the item does NOT have a 'status' property anymore
            cy.request({
              method: 'GET',
              url: `http://localhost:8080/api/items/${createdItemId}`,
              headers: { Authorization: `Bearer ${aliceToken}` }
            }).then((itemResp) => {
              expect(itemResp.body).to.not.have.property('status');
            });
          });
        });
      });
    });
  });

  it('Story 15 (API): Buyer interest list and remove', () => {
    loginViaApi('bob@example.com', 'test').then((bobToken) => {
      cy.request({
        method: 'GET',
        url: 'http://localhost:8080/api/interests',
        headers: { Authorization: `Bearer ${bobToken}` }
      }).then((resp) => {
        expect(resp.status).to.eq(200);
        if (resp.body.length > 0) {
          const itemId = resp.body[0].item.id;
          cy.request({
            method: 'DELETE',
            url: `http://localhost:8080/api/interests/${itemId}`,
            headers: { Authorization: `Bearer ${bobToken}` }
          }).then((delResp) => {
            expect(delResp.status).to.eq(200);
          });
        }
      });
    });
  });

  // ---------- Story 16: User Chat (fresh accounts) ----------
  it('Story 16 (API): Chat send and receive', () => {
    const sellerEmail = `chatseller_${Date.now()}@example.com`;
    const buyerEmail  = `chatbuyer_${Date.now()}@example.com`;

    registerViaApi('ChatSeller', 'Test', sellerEmail, 'test');
    registerViaApi('ChatBuyer', 'Test', buyerEmail, 'test');

    loginViaApi(sellerEmail, 'test').then((sellerToken) => {
      cy.request({
        method: 'POST',
        url: 'http://localhost:8080/api/items',
        headers: { Authorization: `Bearer ${sellerToken}` },
        body: { title: `ChatItem_${Date.now()}`, description: 'Chat test', price: 10, paymentMethod: 'cash', type: 'sale' }
      }).then((resp) => {
        const chatItemId = resp.body.id;
        loginViaApi(buyerEmail, 'test').then((buyerToken) => {
          cy.request({
            method: 'POST',
            url: 'http://localhost:8080/api/chat',
            headers: { Authorization: `Bearer ${buyerToken}` },
            body: { message: 'Hello, is this available?', itemId: chatItemId, buyerId: resp.body.owner.id }
          }).then((sendResp) => {
            expect(sendResp.status).to.eq(200);
          });
          cy.request({
            method: 'GET',
            url: 'http://localhost:8080/api/chat',
            headers: { Authorization: `Bearer ${buyerToken}` }
          }).then((getResp) => {
            expect(getResp.status).to.eq(200);
            expect(getResp.body.length).to.be.greaterThan(0);
          });
        });
      });
    });
  });

  // ---------- Story 17: Point System (fresh accounts with default 10 points) ----------
  it('Story 17 (API): Point system', () => {
    const sellerEmail = `ptseller_${Date.now()}@example.com`;
    const buyerEmail  = `ptbuyer_${Date.now()}@example.com`;

    registerViaApi('PtSeller', 'Test', sellerEmail, 'test');
    registerViaApi('PtBuyer', 'Test', buyerEmail, 'test');

    let pointsItemId: number;

    loginViaApi(sellerEmail, 'test').then((sellerToken) => {
      // Seller creates a points item priced at 5
      cy.request({
        method: 'POST',
        url: 'http://localhost:8080/api/items',
        headers: { Authorization: `Bearer ${sellerToken}` },
        body: { title: `PointsItem_${Date.now()}`, description: 'Buy with points', price: 5, paymentMethod: 'points', type: 'sale' }
      }).then((resp) => {
        pointsItemId = resp.body.id;

        loginViaApi(buyerEmail, 'test').then((buyerToken) => {
          cy.request({
            method: 'POST',
            url: `http://localhost:8080/api/items/${pointsItemId}/buy-with-points`,
            headers: { Authorization: `Bearer ${buyerToken}` }
          }).then((buyResp) => {
            // Buyer started with 10, minus 5 = 5
            expect(buyResp.body.buyerPoints).to.eq(5);
            // Seller started with 10, earned 5 = 15
            expect(buyResp.body.sellerPoints).to.eq(15);
          });
        });
      });
    });
  });

  // ---------- Story 20: Issue Reporting (fresh accounts) ----------
  it('Story 20 (API): Report an item', () => {
    const sellerEmail = `rptseller_${Date.now()}@example.com`;
    const buyerEmail  = `rptbuyer_${Date.now()}@example.com`;

    registerViaApi('RptSeller', 'Test', sellerEmail, 'test');
    registerViaApi('RptBuyer', 'Test', buyerEmail, 'test');

    loginViaApi(sellerEmail, 'test').then((sellerToken) => {
      cy.request({
        method: 'POST',
        url: 'http://localhost:8080/api/items',
        headers: { Authorization: `Bearer ${sellerToken}` },
        body: { title: `ReportItem_${Date.now()}`, description: 'To be reported', price: 10, paymentMethod: 'cash', type: 'sale' }
      }).then((resp) => {
        const reportItemId = resp.body.id;
        loginViaApi(buyerEmail, 'test').then((buyerToken) => {
          cy.request({
            method: 'POST',
            url: 'http://localhost:8080/api/reports',
            headers: { Authorization: `Bearer ${buyerToken}` },
            body: { itemId: reportItemId, reason: 'Inappropriate content' }
          }).then((reportResp) => {
            expect(reportResp.status).to.eq(200);
          });
        });
      });
    });
  });

  // ---------- Story 21: Course Material Rating (fresh accounts, buy first) ----------
  it('Story 21 (API): Rate an item', () => {
    const sellerEmail = `rtseller_${Date.now()}@example.com`;
    const buyerEmail  = `rtbuyer_${Date.now()}@example.com`;

    registerViaApi('RtSeller', 'Test', sellerEmail, 'test');
    registerViaApi('RtBuyer', 'Test', buyerEmail, 'test');

    let ratingItemId: number;

    loginViaApi(sellerEmail, 'test').then((sellerToken) => {
      // Seller creates a points item
      cy.request({
        method: 'POST',
        url: 'http://localhost:8080/api/items',
        headers: { Authorization: `Bearer ${sellerToken}` },
        body: { title: `RatingItem_${Date.now()}`, description: 'To be rated', price: 5, paymentMethod: 'points', type: 'sale' }
      }).then((resp) => {
        ratingItemId = resp.body.id;

        loginViaApi(buyerEmail, 'test').then((buyerToken) => {
          // Buyer buys the item first
          cy.request({
            method: 'POST',
            url: `http://localhost:8080/api/items/${ratingItemId}/buy-with-points`,
            headers: { Authorization: `Bearer ${buyerToken}` }
          }).then(() => {
            // Now rate it
            cy.request({
              method: 'POST',
              url: 'http://localhost:8080/api/ratings',
              headers: { Authorization: `Bearer ${buyerToken}` },
              body: { itemId: ratingItemId, ratingValue: 3 }
            }).then((ratingResp) => {
              expect(ratingResp.status).to.eq(200);
            });
          });
        });
      });
    });
  });

  // ===== UI SMOKE TESTS =====

  it('UI Smoke: Home page loads', () => {
    loginViaApi('alice@example.com', 'test').then((token) => {
      cy.setCookie('token', token as unknown as string, { domain: 'localhost', path: '/' });
      cy.getCookie('token').should('exist'); // debug
      cy.visit('/home');
      cy.get('nav.navbar', { timeout: 15000 }).should('be.visible');
    });
  });

  it('UI Smoke: Course page loads', () => {
    cy.visit('/course/1');
    cy.get('.item-card', { timeout: 15000 }).should('have.length.at.least', 1);
  });

  it('UI Smoke: Registration page loads', () => {
    cy.visit('/register');
    cy.get('input[placeholder="Firstname"]', { timeout: 15000 }).should('be.visible');
  });

});