import React from "react";
import { setBaseUrl } from "../../../utilities/url-utilities";

export default function CopySamples({ children, getSelectedSamples }) {
  const getSamples = () => {
    const selectedSamples = getSelectedSamples();
    sessionStorage.setItem("share", JSON.stringify(selectedSamples));
    window.location.href = setBaseUrl(
      `/projects/${selectedSamples.projectId}/samples-share`
    );
  };

  return React.cloneElement(children, {
    onClick: getSamples,
  });
}
