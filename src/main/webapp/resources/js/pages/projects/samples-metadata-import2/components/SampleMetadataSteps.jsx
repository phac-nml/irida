import React from "react";
import {
    Steps,
} from "antd";

export function SampleMetadataSteps({currentStep}) {
    const { Step } = Steps;

    return (
        <Steps current={currentStep}>
            <Step title="Upload File" />
            <Step title="Map Headers" />
            <Step title="Validation" />
            <Step title="Review Data" />
            <Step title="Completion" />
        </Steps>
    );
}