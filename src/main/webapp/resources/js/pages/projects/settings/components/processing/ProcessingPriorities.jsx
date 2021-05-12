import { Form, notification, Select } from "antd";
import React from "react";
import {
  useGetProjectDetailsQuery,
  useUpdateProjectPriorityMutation,
} from "../../../../../apis/projects/project";

/**
 * Allow the user to modify the priority of pipeline process.
 *
 * @param {number} projectId - project identifier
 * @returns {JSX.Element}
 * @constructor
 */
export function ProcessingPriorities({ projectId }) {
  const [updateProjectPriority] = useUpdateProjectPriorityMutation();
  const { data: project = {} } = useGetProjectDetailsQuery(projectId);
  const PRIORITIES = [
    { value: "LOW", label: i18n("AnalysisDetailsPriority.LOW") },
    { value: "MEDIUM", label: i18n("AnalysisDetailsPriority.MEDIUM") },
    { value: "HIGH", label: i18n("AnalysisDetailsPriority.HIGH") },
  ];

  /**
   * Update the pipeline running priority on the project.
   * @param {string} value - new priority
   */
  const update = (value) =>
    updateProjectPriority({ projectId, priority: value })
      .then((response) =>
        notification.success({ message: response.data.message })
      )
      .catch((error) =>
        notification.error({ message: error.response.data.errror })
      );

  return (
    <Form layout="vertical">
      <Form.Item label={i18n("ProcessingPriorities.title")}>
        <Select
          value={project.priority}
          onChange={update}
          options={PRIORITIES}
        />
      </Form.Item>
    </Form>
  );
}
