import React from "react";
import { render } from "react-dom";
import configureStore from "./redux/configureStore";
import { initializeApp } from "./redux/modules/app";

// Linelist Table
import Root from "./components/Root";

// Get the project id from the window object:
const PROJECT_ID = window.project.id;

const store = configureStore();

// Initialize the data
store.dispatch(initializeApp(PROJECT_ID));

// Render the application
render(<Root store={store} />, document.querySelector("#root"));
