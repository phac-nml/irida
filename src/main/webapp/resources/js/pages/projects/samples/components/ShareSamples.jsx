import React from "react";

export default function ShareSamples({ children }) {
  return React.cloneElement(children, {
    onClick: () => {
      alert("FOOBAR");
    },
  });
}
