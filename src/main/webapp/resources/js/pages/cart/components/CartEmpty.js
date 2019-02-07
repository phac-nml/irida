import React from "react";
import { Row } from "antd";
import { getI18N } from "../../../utilities/i18n-utilties";
import {
  FONT_COLOR_PRIMARY,
  FONT_SIZE_LARGE,
  FONT_WEIGHT_HEAVY
} from "../../../styles/fonts";
import { SPACE_SM } from "../../../styles/spacing";

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
          fontSize: FONT_SIZE_LARGE,
          fontWeight: FONT_WEIGHT_HEAVY,
          color: FONT_COLOR_PRIMARY,
          marginBottom: SPACE_SM
        }}
      >
        {getI18N("CartEmpty.heading")}
      </p>
      <a
        href={`${BASE_URL}projects`}
        style={{
          borderBottom: `2px solid ${FONT_COLOR_PRIMARY}`,
          color: FONT_COLOR_PRIMARY
        }}
      >
        {getI18N("CartEmpty.subheading")}
      </a>
    </Row>
  );
}
