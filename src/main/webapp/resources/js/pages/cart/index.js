import React, { Component } from "react";
import { render } from "react-dom";
import EmptyCart from "./components/EmptyCart";
import { getCartCount } from "../../apis/cart/cart";
import CartContainer from "./components/CartContainer";

class CartPage extends Component {
  state = { loading: true };

  componentDidMount() {
    getCartCount().then(response => {
      this.setState({ loading: false, total: response.count });
    });
  }

  render() {
    const { total, loading } = this.state;

    if (loading) {
      return <div>Loading...</div>;
    } else if (total === 0) {
      return <EmptyCart />;
    }
    return <CartContainer total={total} />;
  }
}

render(<CartPage />, document.querySelector("#root"));
