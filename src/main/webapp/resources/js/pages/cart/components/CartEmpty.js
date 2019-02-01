import React from "react";
import { Row } from "antd";
import { getI18N } from "../../../utilities/i18n-utilties";

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
        alt={getI18N("CartEmpty.imageAlt")}
      />
      <p
        style={{
          fontSize: "1.4em",
          fontWeight: 600,
          color: "#1890ff",
          marginBottom: ".5em"
        }}
      >
        {getI18N("CartEmpty.heading")}
      </p>
      <a
        href={`${BASE_URL}projects`}
        style={{ borderBottom: "2px solid #1890ff" }}
      >
        {getI18N("CartEmpty.subheading")}
      </a>
    </Row>
  );
}
