import React from "react";
import {
    Steps,
} from "antd";

export function SampleMetadataSteps() {
    const { Step } = Steps;

    return (
        <Steps current={0}>
            <Step title="Upload File" />
            <Step title="Verify Headers" />
            <Step title="Review Data" />
            <Step title="Complete" />
        </Steps>
    );
}