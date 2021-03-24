import { unwrapResult } from "@reduxjs/toolkit";
import { Form, notification, Select } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { putPriorityUpdate } from "../../../redux/projectSlice";

/**
 * Allow the user to modify the priority of pipeline process.
 *
 * @param {number} projectId - project identifier
 * @returns {JSX.Element}
 * @constructor
 */
export function ProcessingPriorities({ projectId }) {
  const dispatch = useDispatch();
  const { priority } = useSelector((state) => state.project);
  const PRIORITIES = [
    { value: "LOW", label: i18n("ProcessingPriorities.LOW") },
    { value: "MEDIUM", label: i18n("ProcessingPriorities.MEDIUM") },
    { value: "HIGH", label: i18n("ProcessingPriorities.HIGH") },
  ];

  /**
   * Update the pipeline running priority on the project.
   * @param {string} value - new priority
   */
  const update = (value) =>
    dispatch(putPriorityUpdate({ projectId, priority: value }))
      .then(unwrapResult)
      .then(({ message }) => notification.success({ message }))
      .catch((message) => notification.error({ message }));

  return (
    <Form layout="vertical">
      <Form.Item label={i18n("ProcessingPriorities.title")}>
        <Select value={priority} onChange={update} options={PRIORITIES} />
      </Form.Item>
    </Form>
  );
}
