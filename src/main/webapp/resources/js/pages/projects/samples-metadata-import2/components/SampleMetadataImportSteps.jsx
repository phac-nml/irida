import React from "react";
import {
    Steps,
} from "antd";

const { Step } = Steps;

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