import React from "react";
import { render } from "react-dom";
import { LaunchProvider } from "./launch-context";
import { LaunchPage } from "./LaunchPage";

/**
 * Render page for launching workflow pipelines.
 */
render(
  <LaunchProvider>
    <LaunchPage />
  </LaunchProvider>,
  document.querySelector("#root")
);
