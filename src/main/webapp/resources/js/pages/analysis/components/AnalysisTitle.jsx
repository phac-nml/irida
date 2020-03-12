import React from "react";
import { Error, Running, Success } from "./messages";

export function AnalysisTitle({ state, name }) {
  switch (state) {
    case "COMPLETED":
      return <Success message={name} />;
    case "ERROR":
      return <Error message={name} />;
    default:
      return <Running message={name} />;
  }
}
