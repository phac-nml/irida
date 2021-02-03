import React from "react";
import {
  deleteAnalysisTemplateForProject,
  fetchAnalysisTemplatesForProject,
} from "../../../apis/projects/settings";
import { Button, List, notification, Popconfirm, Tag } from "antd";
import { IconRemove } from "../../../components/icons/Icons";

export function AnalysisTemplates({ projectId }) {
  const [templates, setTemplates] = React.useState();

  React.useEffect(() => {
    fetchAnalysisTemplatesForProject(projectId).then((data) =>
      setTemplates(data)
    );
  }, []);

  const removeAutomatedPipeline = (template) => {
    deleteAnalysisTemplateForProject(template.id, projectId).then((message) => {
      notification.success({ message });
      const filteredTemplates = templates.filter((t) => t.id !== template.id);
      setTemplates(filteredTemplates);
    });
  };

  return (
    <List
      bordered
      dataSource={templates}
      renderItem={(template) => (
        <List.Item
          key={`template-${template.id}`}
          actions={[
            <Popconfirm
              key={`remove-${template.id}`}
              placement="topRight"
              onConfirm={() => removeAutomatedPipeline(template)}
              title={`Are you sure you want to remove the automated pipeline?`}
            >
              <Button shape="circle" icon={<IconRemove />} />
            </Popconfirm>,
          ]}
        >
          <List.Item.Meta
            title={
              <div style={{ display: "flex", justifyContent: "space-between" }}>
                {template.name}
                <Tag color="blue">{template.analysisType}</Tag>
              </div>
            }
            description={template.statusMessage}
          />
        </List.Item>
      )}
    />
  );
}
