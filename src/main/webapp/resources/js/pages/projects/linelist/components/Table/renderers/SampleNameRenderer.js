import React from "react";

const SampleNameRenderer = props =>
  `<a href="${window.TL.BASE_URL}samples/${Number(props.data.sampleId)}">${
    props.value
  }</a>`;

export default SampleNameRenderer;
