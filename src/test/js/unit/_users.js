mocha.setup('bdd');
expect = chai.expect;

describe("Set up test", function () {
  it("Zero equals zero", function () {
    expect(0).to.equal(0);
  });
});

describe("Users", function () {
  "use strict";

  afterEach(function () {
    $.ajax.restore();
  });

  it('Should make an ajax call', function(done){
    sinon.stub($, 'ajax');
    var uvm = ko.applyBindings(new UsersViewModel());
    expect($.ajax.calledOnce).to.be.true;
    done();
  });
});
