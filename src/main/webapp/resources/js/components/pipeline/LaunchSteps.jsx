import React from "react";
import { Steps } from "antd";
import * as STATUS from "../../components/ant.design/step-status";

const { Step } = Steps;

export function LaunchSteps() {
  return (
    <Steps>
      <Step status={STATUS.STEP_PROCESS} title={"TEST 1"} />
      <Step status={STATUS.STEP_WAIT} title={"TEST 2"} />
    </Steps>
  );
}
