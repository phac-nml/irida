import React from "react";
import { grey1, red5 } from "../styles/colors";

export const getI18N = term => {
  const { i18n } = window.PAGE;
  if (typeof i18n === "undefined") {
    throw "No internationalisation's available on the current page";
  } else if (typeof i18n[term] !== "string") {
    return <span style={{ backgroundColor: red5, color: grey1 }}>{term}</span>;
  }
  return i18n[term];
};
