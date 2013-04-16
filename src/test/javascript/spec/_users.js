test("Testing the test", function () {
  expect(1);
  equal(1, 1);
});

//test("Create users lists", function () {
//  var usersVM = new UsersViewModel();
//  equal( usersVM.users.length, 20 );
//});

test("Create users list", function () {
  "use strict";

//  var xhr, requests;
//
//  before(function() {
//    xhr = sinon.useFakeXMLHttpRequest();
//    requests = [];
//    xhr.onCreate = function (req) {
//      requests.push(req);
//    }
//  });
//
//  after(function () {
//    xhr.restore();
//  });
  var server = this.sandbox.useFakeServer();
  server.respondWith(
    "GET", "/users",
    [200, {"Content-Type": "application/json"},
      '[{username:"jadam",firstName:"Josh",lastName:"Adam",email:"jadam@me.com",phoneNumber:"204-999-9876"}]']
  );

  var usersVM = new UsersViewModel();

  equal(1, server.requests.length);
  server.respond();
});