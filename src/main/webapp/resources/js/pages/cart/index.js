import React, { Component } from "react";
import { render } from "react-dom";
import { Row } from "antd";
import { getProjectsInCart } from "../../apis/cart/cart";

const { i18n } = window.PAGE;
const { BASE_URL } = window.TL;

function EmptyCartState() {
  return (
    <Row
      type="flex"
      justify="center"
      align="middle"
      style={{ height: "100%", flexDirection: "column" }}
    >
      <img
        height="300px"
        src={`${BASE_URL}resources/img/empty-cart.svg`}
        alt={i18n.cart.imageAlt}
      />
      <p
        style={{
          fontSize: "1.4em",
          fontWeight: 600,
          color: "#1890ff",
          marginBottom: ".5em"
        }}
      >
        {i18n.cart.empty}
      </p>
      <a
        href={`${BASE_URL}projects`}
        style={{ borderBottom: "2px solid #1890ff" }}
      >
        {i18n.cart.emptySub}
      </a>
    </Row>
  );
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
