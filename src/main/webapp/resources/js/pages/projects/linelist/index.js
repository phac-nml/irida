import React from "react";
import { createRoot } from "react-dom/client";
import { App } from "./App";
import "./linelist.css";
import { setBaseUrl } from "../../../utilities/url-utilities";

__webpack_public_path__ = setBaseUrl(`dist/`);

// Render the application
const ROOT_ELEMENT = document.querySelector("#root");
const root = createRoot(ROOT_ELEMENT);
root.render(<App />);
