import React from "react";
import {
    Steps,
} from "antd";

export function SampleMetadataImportSteps({currentStep}) {
    const { Step } = Steps;

    return (
        <Steps current={currentStep}>
            <Step title="Upload File" />
            <Step title="Map Headers" />
            <Step title="Review Data" />
            <Step title="Complete" />
        </Steps>
    );
}