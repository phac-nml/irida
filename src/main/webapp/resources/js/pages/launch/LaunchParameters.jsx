import React from "react";
import { SectionHeading } from "../../components/ant.design/SectionHeading";
import { SavedParameters } from "./parameters/SavedParameters";
import { LaunchParametersWithOptions } from "./LaunchParametersWithOptions";
import { useLaunch } from "./launch-context";

/**
 * React component to render any parameters required for a pipeline launch
 * @param form
 * @returns {JSX.Element}
 * @constructor
 */
export function LaunchParameters({ form }) {
  const [{ dynamicSources, parameterWithOptions, parameterSets }] = useLaunch();

  return (
    <section>
      <SectionHeading id="launch-parameters">
        {i18n("LaunchParameters.title")}
      </SectionHeading>
      {parameterSets && <SavedParameters form={form} sets={parameterSets} />}
      {parameterWithOptions && (
        <LaunchParametersWithOptions parameters={parameterWithOptions} />
      )}
      {dynamicSources && (
        <LaunchParametersWithOptions parameters={dynamicSources} />
      )}
    </section>
  );
}
