import React from "react";
import { useStateValue } from "./GalaxyState";
import { Alert } from "antd";
import { SPACE_SM } from "../../styles/spacing";
import { getI18N } from "../../utilities/i18n-utilties";

export function GalaxySubmitError() {
  const [{ errored }] = useStateValue();
  return (
    <div>
      {errored ? (
        <Alert
          style={{ marginBottom: SPACE_SM }}
          type="error"
          showIcon
          message={getI18N("GalaxySubmissionError.message")}
          description={getI18N("GalaxySubmissionError.description")}
        />
      ) : null}
    </div>
  );
}
