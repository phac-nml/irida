import React, { Component } from "react";
import { render } from "react-dom";
import EmptyCart from "./components/EmptyCart";
import { getCartSampleIds } from "../../apis/cart/cart";
import CartContainer from "./components/CartContainer";

class CartPage extends Component {
  state = {};

  componentDidMount() {
    getCartSampleIds().then(({ ids }) => {
      this.setState({ total: ids.length, ids });
    });
  }

  render() {
    const { total, ids } = this.state;
    if (typeof total === "undefined") {
      return <div>Loading...</div>;
    } else if (total.length === 0) {
      return <EmptyCart />;
    }
    return <CartContainer total={total} ids={ids} />;
  }
}

render(<CartPage />, document.querySelector("#root"));
