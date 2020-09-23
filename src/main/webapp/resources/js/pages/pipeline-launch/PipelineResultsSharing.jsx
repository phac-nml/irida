import React from "react";
import { Checkbox, Form } from "antd";
import { useLaunchState } from "./launch-context";

export function PipelineResultsSharing() {
  const { updateDetailsField } = useLaunchState();

  return (
    <Form.Item>
      <Checkbox
        onChange={(e) =>
          updateDetailsField({
            field: "shareWithProjects",
            value: e.target.checked,
          })
        }
      >
        {i18n("PipelineDetails.shareWithProjects")}
      </Checkbox>
    </Form.Item>
  );
}
