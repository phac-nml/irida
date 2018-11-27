import React, { Component } from "react";
import { render } from "react-dom";
import EmptyCart from "./components/EmptyCart";
import Cart from "./components/Cart";
import { getCartCount } from "../../apis/cart/cart";

class CartPage extends Component {
  state = {};

  componentDidMount() {
    getCartCount().then(response => {
      this.setState({ count: response.count });
    });
  }

  render() {
    const { count } = this.state;
    if (typeof count === "undefined") {
      return <div>Loading...</div>;
    } else if (count.length === 0) {
      return <EmptyCart />;
    }
    return <Cart total={this.state.count} />;
  }
}

render(<CartPage />, document.querySelector("#root"));
