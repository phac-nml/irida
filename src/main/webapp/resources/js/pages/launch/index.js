import React from "react";
import { createRoot } from "react-dom/client";
import { LaunchProvider } from "./launch-context";
import { LaunchPage } from "./LaunchPage";

/**
 * Render page for launching workflow pipelines.
 */
const root = createRoot(document.querySelector("#root"));
root.render(
  <LaunchProvider>
    <LaunchPage />
  </LaunchProvider>
);
