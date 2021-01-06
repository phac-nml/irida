import React from "react";
import { SectionHeading } from "../../components/ant.design/SectionHeading";
import { SavedParameters } from "./parameters/SavedParameters";
import { ParameterWithOptions } from "./ParameterWithOptions";
import { DynamicSources } from "./DynamicSources";

export function LaunchParameters({ form }) {
  return (
    <div>
      <SectionHeading id="launch-parameters">
        {i18n("LaunchParameters.title")}
      </SectionHeading>
      <SavedParameters form={form} />
      <ParameterWithOptions />
      <DynamicSources />
    </div>
  );
}
