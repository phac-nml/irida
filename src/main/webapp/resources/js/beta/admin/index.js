import * as React from "react";
import { render } from "react-dom";
import { Dashboard } from "../components/dashboad";

function App() {
  return <Dashboard />;
}

render(<App />, document.querySelector("#root"));
