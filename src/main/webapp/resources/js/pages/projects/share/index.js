import React from "react";
import { render } from "react-dom";

function ShareSamples() {
  console.log("FOOBAR");
  return <div>JELLO</div>;
}

render(<ShareSamples />, document.querySelector("#root"));
