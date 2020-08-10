import React from "react";
import { Steps } from "antd";
import * as STATUS from "../../components/ant.design/step-status";
import { IconFile, IconForm } from "../icons/Icons";
import { useLaunchState } from "./launch-context";

const { Step } = Steps;

export function LaunchSteps() {
  const { requiresReference } = useLaunchState();

  return (
    <Steps>
      <Step
        status={STATUS.STEP_PROCESS}
        title={"PIPELINE DETAILS"}
        icon={<IconForm />}
      />
      {requiresReference ? (
        <Step
          status={STATUS.STEP_WAIT}
          title={"REFERENCE FILES"}
          icon={<IconFile />}
        />
      ) : null}
    </Steps>
  );
}
