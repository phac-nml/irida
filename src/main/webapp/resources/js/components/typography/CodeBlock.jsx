import React from "react";

export function CodeBlock({ children }) {
  return <pre style={{ whiteSpace: "pre-wrap" }}>{children}</pre>;
}
