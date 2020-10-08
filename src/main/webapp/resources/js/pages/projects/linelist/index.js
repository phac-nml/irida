import React from "react";
import { render } from "react-dom";
import { App } from "./App";
import "./linelist.css";
import { setBaseUrl } from "../../../utilities/url-utilities";

__webpack_public_path__ = setBaseUrl(`dist/`);

// Render the application
render(<App />, document.querySelector("#root"));
