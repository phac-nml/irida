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
    <Steps current={currentStep}>
      <Step title="Upload File" />
      <Step title="Map Headers" />
      <Step title="Review Data" />
      <Step title="Complete" />
    </Steps>
  );
}