import React from "react";
import PropTypes from "prop-types";
import { GalaxySamples } from "./GalaxySamples";
import { GalaxyOauth } from "./GalaxyOauth";
import { GalaxyFinalSubmission } from "./GalaxyFinalSubmission";

export function GalaxySubmissionSteps({ setSamples, setOauth, query }) {
  return (
    <>
      <GalaxySamples setSamples={setSamples} />
      <GalaxyOauth setOauth={setOauth} />
      {query !== undefined ? <GalaxyFinalSubmission query={query} /> : null}
    </>
  );
}

GalaxySubmissionSteps.propTypes = {
  setOauth: PropTypes.func.isRequired,
  setSamples: PropTypes.func.isRequired
};
