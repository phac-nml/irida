import { Select, Table } from "antd";
import React from "react";
import {
  getMetadataRestrictions,
  useGetMetadataFieldsForProjectQuery,
} from "../../../../apis/metadata/field";

export function ShareMetadataFields({ projectId, destinationId }) {
  const [fields, setFields] = React.useState();
  const [restrictions, setRestrictions] = React.useState([]);

  const ROLES = {
    PROJECT_USER: i18n("projectRole.PROJECT_USER"),
    PROJECT_OWNER: i18n("projectRole.PROJECT_OWNER"),
  };

  const {
    data: currentFields,
    isLoading: currentLoading,
  } = useGetMetadataFieldsForProjectQuery(projectId);
  const {
    data: destinationFields,
    isLoading: destinationLoading,
  } = useGetMetadataFieldsForProjectQuery(destinationId, {
    skip: destinationId === undefined,
  });

  React.useEffect(() => {
    getMetadataRestrictions().then(setRestrictions);
  }, []);

  React.useEffect(() => {
    if (!currentLoading && !destinationLoading) {
      const merged = currentFields.map((current) => {
        const target = destinationFields?.find(
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
  }, [destinationFields, destinationLoading, currentFields, currentLoading]);

  return (
    <div>
      <Table
        loading={currentLoading && destinationLoading}
        rowKey={(item) => `field-${item.current.id}`}
        locale={{
          emptyText:
            "__There are no metadata fields being copied for these samples__",
        }}
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
                <Select style={{ display: "inline-block" }} defaultValue={text}>
                  {restrictions.map((restriction) => (
                    <Select.Option
                      key={restriction.value}
                      value={restriction.value}
                    >
                      {restriction.label}
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
