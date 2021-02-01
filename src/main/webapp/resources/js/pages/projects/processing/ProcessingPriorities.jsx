import React from "react";
import {
  fetchProcessingInformation,
  updateProcessingPriority,
} from "../../../apis/projects/settings";
import { Form, notification, Select } from "antd";

export function ProcessingPriorities({ projectId }) {
  const [priorities, setPriorities] = React.useState([]);
  const [priority, setPriority] = React.useState();

  const getProcessingInfo = React.useCallback(async () => {
    const data = await fetchProcessingInformation(projectId);
    setPriorities(data.priorities);
    setPriority(data.priority);
  }, [projectId]);

  React.useEffect(() => {
    getProcessingInfo();
  }, [getProcessingInfo]);

  const update = (value) => {
    updateProcessingPriority(projectId, value).then((message) => {
      setPriority(value);
      notification.success({ message });
    });
  };

  return (
    <Form layout="vertical">
      <Form.Item label={"Analysis Priority"}>
        <Select options={priorities} value={priority} onChange={update} />
      </Form.Item>
    </Form>
  );
}
