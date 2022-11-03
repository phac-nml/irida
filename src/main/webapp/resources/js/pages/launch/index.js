import React from "react";
import { createRoot } from "react-dom/client";
import { LaunchProvider } from "./launch-context";
import { LaunchPage } from "./LaunchPage";

/**
 * Render page for launching workflow pipelines.
 */
const ROOT_ELEMENT = document.querySelector("#root");
const root = createRoot(ROOT_ELEMENT);
root.render(
  <LaunchProvider>
    <LaunchPage />
  </LaunchProvider>
);
