import * as React from "react";
import { render } from "react-dom";
import { Dashboard } from "../components/dashboad";
import { navigationItems } from "./navigationItems";
import { BrowserRouter as Router } from "react-router-dom";

function App() {
  return <Dashboard navigation={navigationItems} title={"IRIDA Admin"} />;
}

render(
  <Router basename={"/beta/admin"}>
    <App />
  </Router>,
  document.querySelector("#root")
);
