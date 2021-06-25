import { CheckCircleTwoTone, CloseCircleTwoTone } from "@ant-design/icons";
import { Select, Space, Table, Tag } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  useGetMetadataFieldsForProjectQuery,
  useGetMetadataRestrictionsQuery,
} from "../../../../apis/metadata/field";
import { green6, red6 } from "../../../../styles/colors";
import { setFields, updateFields } from "../services/rootReducer";

export function ShareMetadataFields({ projectId }) {
  const dispatch = useDispatch();
  const { destinationId, fields = [] } = useSelector((state) => state.reducer);
  const { data: restrictions } = useGetMetadataRestrictionsQuery();
  console.log(fields);

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
      console.log(merged);
      dispatch(setFields(merged));
    }
  }, [destinationFields, destinationLoading, currentFields, currentLoading]);

  const updateRestriction = (index, value) => {
    dispatch(updateFields(index, value));
  };

  const getIcon = (item) => {
    const current = restrictions.findIndex(
      (restriction) => restriction.value === item.current.restriction
    );
    const target = restrictions.findIndex(
      (restriction) => restriction.value === item.target.restriction
    );
    console.log({ current, target, item });
    if (current > target) return <CloseCircleTwoTone twoToneColor={red6} />;
    return <CheckCircleTwoTone twoToneColor={green6} />;
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
                    style={{ display: "inline-block", width: 150 }}
                    defaultValue={text}
                    onChange={(value) => updateRestriction(index, value)}
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
