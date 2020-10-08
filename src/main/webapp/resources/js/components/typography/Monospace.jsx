/*
 * Use this to display text characters using
 * fixed width. An example of use: displaying
 * ids
 */

import React from "react";

export function Monospace({ children }) {
  return <span style={{ fontFamily: "monospace" }}>{children}</span>;
}
