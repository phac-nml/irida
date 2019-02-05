import React from "react";
import { Row } from "antd";
import { getI18N } from "../../../utilities/i18n-utilties";
import { COLOURS, FONTS, SPACING } from "../../../styles";

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
          fontSize: FONTS.SIZE_LG,
          fontWeight: FONTS.WEIGHT_HEAVY,
          color: COLOURS.TEXT_HIGHLIGHTED,
          marginBottom: SPACING.DEFAULT
        }}
      >
        {getI18N("CartEmpty.heading")}
      </p>
      <a
        href={`${BASE_URL}projects`}
        style={{
          borderBottom: `2px solid ${COLOURS.TEXT_HIGHLIGHTED}`,
          color: COLOURS.TEXT_HIGHLIGHTED
        }}
      >
        {getI18N("CartEmpty.subheading")}
      </a>
    </Row>
  );
}
