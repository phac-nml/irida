import React from "react";
import { SectionHeading } from "../../components/ant.design/SectionHeading";
import { SavedParameters } from "./parameters/SavedParameters";
import { ParameterWithOptions } from "./ParameterWithOptions";
import { useLaunch } from "./launch-context";

/**
 * React component to render any parameters required for a pipeline launch
 * @param form
 * @returns {JSX.Element}
 * @constructor
 */
export function LaunchParameters({ form }) {
  const [{ dynamicSources, parameterWithOptions }] = useLaunch();
  console.log({ dynamicSources, parameterWithOptions });
  return (
    <section>
      <SectionHeading id="launch-parameters">
        {i18n("LaunchParameters.title")}
      </SectionHeading>
      <SavedParameters form={form} />
      {parameterWithOptions && (
        <ParameterWithOptions parameters={parameterWithOptions} />
      )}
      {dynamicSources && <ParameterWithOptions parameters={dynamicSources} />}
    </section>
  );
}
