import React from "react";
import { Card, Checkbox, Form } from "antd";
import { useLaunchState } from "./launch-context";

export function PipelineResultsSharing() {
  const { api } = useLaunchState();

  return (
    <Card title={i18n("LaunchContent.resultSharing")}>
      <Form.Item>
        <Checkbox
          onChange={(e) =>
            api.updateDetailsField({
              field: "shareWithProjects",
              value: e.target.checked,
            })
          }
        >
          {i18n("PipelineDetails.shareWithProjects")}
        </Checkbox>
      </Form.Item>
    </Card>
  );
}
