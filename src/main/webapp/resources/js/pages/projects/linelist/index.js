import React from "react";
import { createRoot } from "react-dom/client";
import { App } from "./App";
import "./linelist.css";
import { setBaseUrl } from "../../../utilities/url-utilities";

__webpack_public_path__ = setBaseUrl(`dist/`);

// Render the app
const root = createRoot(document.querySelector("#root"));
root.render(<App />);
