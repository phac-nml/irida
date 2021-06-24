import { Select, Table } from "antd";
import React from "react";
import {
  getMetadataRestrictions,
  useGetMetadataFieldsForProjectQuery,
} from "../../../../apis/metadata/field";

export function ShareMetadataFields({ projectId, sharedProjectId }) {
  const [fields, setFields] = React.useState();
  const [restrictions, setRestrictions] = React.useState([]);

  console.log({ projectId, sharedProjectId });

  const ROLES = {
    PROJECT_USER: i18n("projectRole.PROJECT_USER"),
    PROJECT_OWNER: i18n("projectRole.PROJECT_OWNER"),
  };

  const {
    data: sharedFields,
    isLoading: sharedLoading,
  } = useGetMetadataFieldsForProjectQuery(projectId);
  const {
    data: projectFields,
    isLoading: projectsLoading,
  } = useGetMetadataFieldsForProjectQuery(sharedProjectId);

  React.useEffect(() => {
    getMetadataRestrictions().then(setRestrictions);
  }, []);

  React.useEffect(() => {
    if (!sharedLoading && !projectsLoading) {
      const merged = sharedFields.map((current) => {
        const target = projectFields.find(
          (element) => element.label === current.label
        );
        return {
          current,
          target:
            target !== undefined
              ? { ...target, exists: true }
              : { ...current, exists: false },
        };
      });
      setFields(merged);
    }
  }, [projectFields, projectsLoading, sharedFields, sharedLoading]);

  return (
    <div>
      <Table
        rowKey={(item) => `field-${item.current.id}`}
        columns={[
          { title: "Field Label", dataIndex: ["current", "label"] },
          {
            title: "Current Project Restrictions",
            dataIndex: ["current", "restriction"],
            render: (text) => ROLES[text],
          },
          {
            title: "Target Project Restrictions",
            dataIndex: ["target", "restriction"],
            render: (text, item) => {
              return (
                <Select
                  style={{ display: "block" }}
                  defaultValue={{ value: text }}
                >
                  {restrictions.map((restriction) => (
                    <Select.Option
                      key={restriction.value}
                      value={restriction.value}
                    >
                      {restriction.text}
                    </Select.Option>
                  ))}
                </Select>
              );
            },
          },
        ]}
        dataSource={fields}
      />
    </div>
  );
}
