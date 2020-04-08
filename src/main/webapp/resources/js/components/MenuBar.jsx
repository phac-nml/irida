import React from "react";
import { SPACE_XS } from "../styles/spacing";

/**
 * React component to hold menu options for a table.
 * @param {element} children - the actual buttons of the toolbar
 * @returns {*}
 * @constructor
 */
export function MenuBar({ children }) {
  return <div style={{ marginBottom: SPACE_XS }}>{children}</div>;
}
