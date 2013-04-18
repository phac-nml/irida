module("Users Tests");

//$.mockjax({
//  url         : '/users',
//  responseText: {
//    "userResources": {
//      "links"     : [
//        {
//          "rel" : "first",
//          "href": "http://0.0.0.0:8080/users?page=1&size=20&sortColumn=username&sortOrder=ASCENDING"
//        },
//        {
//          "rel" : "next",
//          "href": "http://0.0.0.0:8080/users?page=2&size=20&sortColumn=username&sortOrder=ASCENDING"
//        },
//        {
//          "rel" : "last",
//          "href": "http://0.0.0.0:8080/users?page=2&size=20&sortColumn=username&sortOrder=ASCENDING"
//        },
//        {
//          "rel" : "self",
//          "href": "http://0.0.0.0:8080/users?page=1&size=20&sortColumn=username&sortOrder=ASCENDING"
//        }
//      ],
//      "users"     : [
//        {
//          "links"      : [
//            {
//              "rel" : "self",
//              "href": "http://0.0.0.0:8080/users/badam"
//            }
//          ],
//          "username"   : "badam",
//          "email"      : "n@me.com",
//          "uri"        : null,
//          "firstName"  : "Ninja",
//          "lastName"   : "Penner",
//          "phoneNumber": "787-5678"
//        },
//        {
//          "links"      : [
//            {
//              "rel" : "self",
//              "href": "http://0.0.0.0:8080/users/cadam"
//            }
//          ],
//          "username"   : "cadam",
//          "email"      : "n@me.com",
//          "uri"        : null,
//          "firstName"  : "Ninja",
//          "lastName"   : "Penner",
//          "phoneNumber": "787-5678"
//        },
//        {
//          "links"      : [
//            {
//              "rel" : "self",
//              "href": "http://0.0.0.0:8080/users/dadam"
//            }
//          ],
//          "username"   : "dadam",
//          "email"      : "n@me.com",
//          "uri"        : null,
//          "firstName"  : "Ninja",
//          "lastName"   : "Penner",
//          "phoneNumber": "787-5678"
//        },
//        {
//          "links"      : [
//            {
//              "rel" : "self",
//              "href": "http://0.0.0.0:8080/users/eadam"
//            }
//          ],
//          "username"   : "eadam",
//          "email"      : "n@me.com",
//          "uri"        : null,
//          "firstName"  : "Ninja",
//          "lastName"   : "Penner",
//          "phoneNumber": "787-5678"
//        },
//        {
//          "links"      : [
//            {
//              "rel" : "self",
//              "href": "http://0.0.0.0:8080/users/fadam"
//            }
//          ],
//          "username"   : "fadam",
//          "email"      : "n@me.com",
//          "uri"        : null,
//          "firstName"  : "Ninja",
//          "lastName"   : "Penner",
//          "phoneNumber": "787-5678"
//        },
//        {
//          "links"      : [
//            {
//              "rel" : "self",
//              "href": "http://0.0.0.0:8080/users/gadam"
//            }
//          ],
//          "username"   : "gadam",
//          "email"      : "n@me.com",
//          "uri"        : null,
//          "firstName"  : "Ninja",
//          "lastName"   : "Penner",
//          "phoneNumber": "787-5678"
//        },
//        {
//          "links"      : [
//            {
//              "rel" : "self",
//              "href": "http://0.0.0.0:8080/users/hadam"
//            }
//          ],
//          "username"   : "hadam",
//          "email"      : "n@me.com",
//          "uri"        : null,
//          "firstName"  : "Ninja",
//          "lastName"   : "Penner",
//          "phoneNumber": "787-5678"
//        },
//        {
//          "links"      : [
//            {
//              "rel" : "self",
//              "href": "http://0.0.0.0:8080/users/hjadam"
//            }
//          ],
//          "username"   : "hjadam",
//          "email"      : "h@me.com",
//          "uri"        : null,
//          "firstName"  : "Hammy",
//          "lastName"   : "Penner",
//          "phoneNumber": "787-1234"
//        },
//        {
//          "links"      : [
//            {
//              "rel" : "self",
//              "href": "http://0.0.0.0:8080/users/iadam"
//            }
//          ],
//          "username"   : "iadam",
//          "email"      : "n@me.com",
//          "uri"        : null,
//          "firstName"  : "Ninja",
//          "lastName"   : "Penner",
//          "phoneNumber": "787-5678"
//        },
//        {
//          "links"      : [
//            {
//              "rel" : "self",
//              "href": "http://0.0.0.0:8080/users/jadam"
//            }
//          ],
//          "username"   : "jadam",
//          "email"      : "n@me.com",
//          "uri"        : null,
//          "firstName"  : "Ninja",
//          "lastName"   : "Penner",
//          "phoneNumber": "787-5678"
//        },
//        {
//          "links"      : [
//            {
//              "rel" : "self",
//              "href": "http://0.0.0.0:8080/users/jkgtffjh"
//            }
//          ],
//          "username"   : "jkgtffjh",
//          "email"      : "n@me.com",
//          "uri"        : null,
//          "firstName"  : "Ninja",
//          "lastName"   : "Penner",
//          "phoneNumber": "787-5678"
//        },
//        {
//          "links"      : [
//            {
//              "rel" : "self",
//              "href": "http://0.0.0.0:8080/users/jkhkjh"
//            }
//          ],
//          "username"   : "jkhkjh",
//          "email"      : "n@me.com",
//          "uri"        : null,
//          "firstName"  : "Ninja",
//          "lastName"   : "Penner",
//          "phoneNumber": "787-5678"
//        },
//        {
//          "links"      : [
//            {
//              "rel" : "self",
//              "href": "http://0.0.0.0:8080/users/jlky"
//            }
//          ],
//          "username"   : "jlky",
//          "email"      : "n@me.com",
//          "uri"        : null,
//          "firstName"  : "Ninja",
//          "lastName"   : "Penner",
//          "phoneNumber": "787-5678"
//        },
//        {
//          "links"      : [
//            {
//              "rel" : "self",
//              "href": "http://0.0.0.0:8080/users/jlouh"
//            }
//          ],
//          "username"   : "jlouh",
//          "email"      : "n@me.com",
//          "uri"        : null,
//          "firstName"  : "Ninja",
//          "lastName"   : "Penner",
//          "phoneNumber": "787-5678"
//        },
//        {
//          "links"      : [
//            {
//              "rel" : "self",
//              "href": "http://0.0.0.0:8080/users/jsadam"
//            }
//          ],
//          "username"   : "jsadam",
//          "email"      : "j@me.com",
//          "uri"        : null,
//          "firstName"  : "Jake",
//          "lastName"   : "Penner",
//          "phoneNumber": "787-9998"
//        },
//        {
//          "links"      : [
//            {
//              "rel" : "self",
//              "href": "http://0.0.0.0:8080/users/kadam"
//            }
//          ],
//          "username"   : "kadam",
//          "email"      : "n@me.com",
//          "uri"        : null,
//          "firstName"  : "Ninja",
//          "lastName"   : "Penner",
//          "phoneNumber": "787-5678"
//        },
//        {
//          "links"      : [
//            {
//              "rel" : "self",
//              "href": "http://0.0.0.0:8080/users/klj;lkkj;l"
//            }
//          ],
//          "username"   : "klj;lkkj;l",
//          "email"      : "n@me.com",
//          "uri"        : null,
//          "firstName"  : "Ninja",
//          "lastName"   : "Penner",
//          "phoneNumber": "787-5678"
//        },
//        {
//          "links"      : [
//            {
//              "rel" : "self",
//              "href": "http://0.0.0.0:8080/users/kljkhjlkjlk"
//            }
//          ],
//          "username"   : "kljkhjlkjlk",
//          "email"      : "n@me.com",
//          "uri"        : null,
//          "firstName"  : "Ninja",
//          "lastName"   : "Penner",
//          "phoneNumber": "787-5678"
//        },
//        {
//          "links"      : [
//            {
//              "rel" : "self",
//              "href": "http://0.0.0.0:8080/users/kljlkj"
//            }
//          ],
//          "username"   : "kljlkj",
//          "email"      : "n@me.com",
//          "uri"        : null,
//          "firstName"  : "Ninja",
//          "lastName"   : "Penner",
//          "phoneNumber": "787-5678"
//        },
//        {
//          "links"      : [
//            {
//              "rel" : "self",
//              "href": "http://0.0.0.0:8080/users/ladam"
//            }
//          ],
//          "username"   : "ladam",
//          "email"      : "n@me.com",
//          "uri"        : null,
//          "firstName"  : "Ninja",
//          "lastName"   : "Penner",
//          "phoneNumber": "787-5678"
//        }
//      ],
//      "totalUsers": 26
//    },
//    "pageTitle"    : "Users",
//    "users"        : true
//  }
//
//
//});

test("Generic test", function () {
  expect(1);
  var poo = 1;
  equal(poo, 1, "Should pass");
});

asyncTest("Async setup", function () {
//  console.log("Entering testing function");
//  var uvm = new UsersViewModel();
//  ko.applyBindings(uvm);

  expect( 1 );
  uvm = new UsersViewModel();
  ko.applyBindings(uvm);
  setTimeout(function() {
    ok( true, "Passed and ready to resume!" );
    console.log(uvm.users());
    start();
  }, 5000);
});

