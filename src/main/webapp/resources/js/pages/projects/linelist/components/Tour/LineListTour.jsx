import React from "react";
import Tour from "reactour";
import { steps } from "./steps";

export default function LineListTour(props) {
  return (
    <Tour
      rounded={4}
      steps={steps}
      isOpen={props.isOpen}
      onRequestClose={props.closeTour}
    />
  );
}