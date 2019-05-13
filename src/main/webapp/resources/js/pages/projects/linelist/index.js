import React from "react";
import { render } from "react-dom";
import { App } from "./App";
import "./linelist.scss";

__webpack_public_path__ = `${window.TL.BASE_URL}dist/`;

// Render the application
render(<App />, document.querySelector("#root"));
