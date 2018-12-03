import React from "react";
import { Row } from "antd";

const { i18n } = window.PAGE;
const { BASE_URL } = window.TL;

export default function CartEmpty() {
  return (
    <Row
      type="flex"
      justify="center"
      align="middle"
      style={{ height: "100%", flexDirection: "column", padding: 100 }}
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
