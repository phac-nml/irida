import React from "react";
import {useStateValue} from "./GalaxyState";
import { Alert } from "antd";
import { SPACE_SM } from "../../styles/spacing";

export function GalaxySubmitError() {
  const [{ errored }] = useStateValue();
  return (
    <div>
      {errored ? (
        <Alert
          style={{ marginBottom: SPACE_SM }}
          type="error"
          showIcon
          message={__("GalaxySubmissionError.message")}
          description={__("GalaxySubmissionError.description")}
        />
      ) : null}
    </div>
  );
}
