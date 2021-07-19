import React from "react";
import {
  Steps,
} from "antd";

const { Step } = Steps;

/**
 * React component that displays the steps for the Sample Metadata Uploader.
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportSteps({ currentStep }) {
  return (
    <Steps current={currentStep} >
      <Step title={i18n("SampleMetadataImportSteps.step1")} />
      <Step title={i18n("SampleMetadataImportSteps.step2")} />
      <Step title={i18n("SampleMetadataImportSteps.step3")} />
      <Step title={i18n("SampleMetadataImportSteps.step4")} />
    </Steps>
  );
}