import React from "react";
import { createRoot } from 'react-dom/client';
import { LaunchProvider } from "./launch-context";
import { LaunchPage } from "./LaunchPage";

/**
 * Render page for launching workflow pipelines.
 */
const container = document.getElementById('root');
const root = createRoot(container);
root.render(
  <LaunchProvider>
    <LaunchPage />
  </LaunchProvider>
);
