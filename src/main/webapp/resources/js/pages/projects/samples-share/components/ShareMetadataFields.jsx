import { Select, Space, Table, Tag } from "antd";
import React from "react";
import { useSelector } from "react-redux";
import {
  useGetMetadataFieldsForProjectQuery,
  useGetMetadataRestrictionsQuery,
} from "../../../../apis/metadata/field";

export function ShareMetadataFields({ projectId }) {
  const [fields, setFields] = React.useState();
  const { destinationId } = useSelector((state) => state.reducer);
  const { data: restrictions } = useGetMetadataRestrictionsQuery();

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

  const getIcon = (item) => {
    const current = restrictions.findIndex(
      (restriction) => restriction.value === item.current.restriction
    );
    const target = restrictions.findIndex(
      (restriction) => restriction.value === item.target.restriction
    );
    console.log({ current, target, item });
  };

  return (
    <div>
      <Table
        loading={currentLoading && destinationLoading}
        pagination={{ hideOnSinglePage: true, pageSize: fields?.length }}
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
            title: "Destination Project Restrictions",
            dataIndex: ["target", "restriction"],
            render: (text, item, index) => {
              getIcon(item);
              return (
                <Space>
                  {getIcon(item)}
                  <Select
                    style={{ display: "inline-block" }}
                    defaultValue={text}
                  >
                    {restrictions.map((restriction) => (
                      <Select.Option
                        key={restriction.value}
                        value={restriction.value}
                      >
                        {restriction.label}
                      </Select.Option>
                    ))}
                  </Select>
                  {!item.target.exists && <Tag color="green">NEW</Tag>}
                </Space>
              );
            },
          },
        ]}
        dataSource={fields}
      />
    </div>
  );
}
