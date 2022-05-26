import * as React from "react";
import { render } from "react-dom";
import { Dashboard } from "../components/dashboad";
import { navigationItems } from "./navigationItems";

function App() {
  return <Dashboard navigation={navigationItems} title={"IRIDA Admin"} />;
}

render(<App />, document.querySelector("#root"));
