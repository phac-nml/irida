import React from "react";
import { Steps } from "antd";
import { ImportState, useImportSelector } from "../redux/store";

const { Step } = Steps;

/**
 * React component that displays the steps for the Sample Metadata Uploader.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportSteps(): JSX.Element {
  const { stepCurrent, stepStatus, percentComplete } = useImportSelector(
    (state: ImportState) => state.importReducer
  );
  return (
    <Steps current={stepCurrent} status={stepStatus} percent={percentComplete}>
      <Step title={i18n("SampleMetadataImportSteps.step1")} />
      <Step title={i18n("SampleMetadataImportSteps.step2")} />
      <Step title={i18n("SampleMetadataImportSteps.step3")} />
      <Step title={i18n("SampleMetadataImportSteps.step4")} />
    </Steps>
  );
}
