/*
 * Use this to display text with spaces and line breaks
 * which are preserved. Some examples of use: displaying
 * code, tool parameters, standard errors/output
 */

import React from "react";

export function CodeBlock({ children }) {
  return <pre style={{ whiteSpace: "pre-wrap" }}>{children}</pre>;
}
