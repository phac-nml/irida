import React, { Component } from "react";
import { render } from "react-dom";
import EmptyCart from "./components/EmptyCart";
import { getCartCount } from "../../apis/cart/cart";
import CartContainer from "./components/CartContainer";

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
    return <CartContainer total={this.state.count} />;
  }
}

render(<CartPage />, document.querySelector("#root"));
