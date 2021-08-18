import React from "react";
import { render } from "react-dom";

function ShareSamples() {
  React.useEffect(() => {
    const stringData = window.sessionStorage.getItem("share");
    const data = JSON.parse(stringData);
    console.log(data);
  }, []);
  return <div>JELLO</div>;
}

render(<ShareSamples />, document.querySelector("#root"));
