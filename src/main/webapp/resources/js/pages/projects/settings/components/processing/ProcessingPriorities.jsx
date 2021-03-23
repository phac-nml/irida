import { unwrapResult } from "@reduxjs/toolkit";
import { Form, notification, Select } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  fetchPipelinePriorityInfo,
  putPriorityUpdate,
} from "../../../redux/pipelinesSlice";

/**
 * Allow the user to modify the priority of pipeline process.
 *
 * @param {number} projectId - project identifier
 * @returns {JSX.Element}
 * @constructor
 */
export function ProcessingPriorities({ projectId }) {
  const dispatch = useDispatch();
  const { priority, priorities } = useSelector((state) => state.pipelines);

  React.useEffect(async () => {
    dispatch(fetchPipelinePriorityInfo(projectId));
  }, []);

  const update = (value) =>
    dispatch(putPriorityUpdate({ projectId, priority: value }))
      .then(unwrapResult)
      .then(({ message }) => notification.success({ message }))
      .catch((message) => notification.error({ message }));

  return (
    <Form layout="vertical">
      <Form.Item label={i18n("ProcessingPriorities.title")}>
        <Select options={priorities} value={priority} onChange={update} />
      </Form.Item>
    </Form>
  );
}
