import React from "react";
import { Steps, StepsProps } from "antd";

const { Step } = Steps;

/**
 * React component that displays the steps for the Sample Metadata Uploader.
 * @prop current - the current step, starting with zero
 * @prop status - the status of the current step
 * @prop percent - the progress percentage of the current step
 * @returns {*}
 * @constructor
 */
export function SampleMetadataImportSteps({
  current,
  status,
  percent,
}: StepsProps): JSX.Element {
  return (
    <Steps current={current} status={status} percent={percent}>
      <Step title={i18n("SampleMetadataImportSteps.step1")} />
      <Step title={i18n("SampleMetadataImportSteps.step2")} />
      <Step title={i18n("SampleMetadataImportSteps.step3")} />
      <Step title={i18n("SampleMetadataImportSteps.step4")} />
    </Steps>
  );
}
