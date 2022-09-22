import React from "react";
import { Steps } from "antd";

const { Step } = Steps;

/**
 * React component that displays the steps for the Sample Metadata Uploader.
 * @prop {number} currentStep - the current step, starting with zero
 * @prop {string} currentStatus - the status of the current step
 * @prop {string} currentPercent - the progress percentage of the current step
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportSteps({
  currentStep,
  currentStatus,
  currentPercent,
}) {
  return (
    <Steps
      current={currentStep}
      status={currentStatus}
      percent={currentPercent}
    >
      <Step title={i18n("SampleMetadataImportSteps.step1")} />
      <Step title={i18n("SampleMetadataImportSteps.step2")} />
      <Step title={i18n("SampleMetadataImportSteps.step3")} />
      <Step title={i18n("SampleMetadataImportSteps.step4")} />
    </Steps>
  );
}
