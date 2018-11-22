import React, { Component } from "react";
import { render } from "react-dom";
import { getProjectsInCart } from "../../apis/cart/cart";

function EmptyCartState() {
  return <h1>EMPTY</h1>;
}

class Cart extends Component {
  state = {};

  componentDidMount() {
    getProjectsInCart().then(data => {
      this.setState({ projects: data });
    });
  }

  render() {
    const { projects } = this.state;
    if (typeof projects === "undefined") {
      return <div>Loading...</div>;
    } else if (projects.length === 0) {
      return <EmptyCartState />;
    }
    return null;
  }
}

render(<Cart />, document.querySelector("#root"));
