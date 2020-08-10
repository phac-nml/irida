import React from "react";
import { Steps } from "antd";
import * as STATUS from "../../components/ant.design/step-status";
import { IconFile, IconForm } from "../icons/Icons";

const { Step } = Steps;

export function LaunchSteps() {
  return (
    <Steps>
      <Step
        status={STATUS.STEP_PROCESS}
        title={"PIPELINE DETAILS"}
        icon={<IconForm />}
      />
      <Step
        status={STATUS.STEP_WAIT}
        title={"REFERENCE FILES"}
        icon={<IconFile />}
      />
    </Steps>
  );
}
