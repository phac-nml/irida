import { navigate } from "@reach/router";
import { Button, Form, Select, Space, Table, Tag } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  useGetMetadataFieldsForProjectQuery,
  useGetMetadataRestrictionsQuery,
} from "../../../../apis/metadata/field";
import { IconArrowLeft } from "../../../../components/icons/Icons";
import { setFields, updateFields } from "../services/rootReducer";

export function ShareMetadataFields({ projectId }) {
  const dispatch = useDispatch();
  const { destinationId, fields = [] } = useSelector((state) => state.reducer);
  const { data: restrictions = [] } = useGetMetadataRestrictionsQuery();

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
      dispatch(setFields(merged));
    }
  }, [destinationFields, destinationLoading, currentFields, currentLoading]);

  const updateRestriction = (index, value) => {
    dispatch(updateFields(index, value));
  };

  const renderRestriction = (text, item, index) => {
    const current = restrictions.findIndex(
      (restriction) => restriction.value === item.current.restriction
    );
    const destination = restrictions.findIndex(
      (restriction) => restriction.value === item.target.restriction
    );

    const loweredPermission = current > destination;
    const higherPermission = current < destination;

    const feedback = {
      hasFeedback: true,
      validateStatus: loweredPermission ? "warning" : "success",
      help: loweredPermission
        ? "This field has a lower permission"
        : higherPermission
        ? "This field will be more secure in the destination project"
        : "Permissions have not been changed",
    };

    return (
      <Form.Item {...feedback}>
        <Select
          defaultValue={text}
          onChange={(value) => updateRestriction(index, value)}
        >
          {restrictions.map((restriction) => (
            <Select.Option key={restriction.value} value={restriction.value}>
              {restriction.label}
            </Select.Option>
          ))}
        </Select>
      </Form.Item>
    );
  };

  return (
    <Space direction="vertical" style={{ display: "block" }}>
      <Form>
        <Table
          loading={currentLoading && destinationLoading}
          pagination={{ hideOnSinglePage: true, pageSize: fields?.length }}
          rowKey={(item) => `field-${item.current.id}`}
          locale={{
            emptyText:
              "__There are no metadata fields being copied for these samples__",
          }}
          columns={[
            {
              title: "Field",
              dataIndex: ["current", "label"],
              render: (text, item) => {
                return (
                  <Space>
                    {text}
                    {!item.target.exists && <Tag color="green">NEW</Tag>}
                  </Space>
                );
              },
            },
            {
              title: "Current Project Restrictions",
              dataIndex: ["current", "restriction"],
              render: (text) => ROLES[text],
            },
            {
              title: "Destination Project Restrictions",
              dataIndex: ["target", "restriction"],
              render: renderRestriction,
              width: 400,
            },
          ]}
          dataSource={fields}
        />
      </Form>
      <div>
        <Button onClick={() => navigate("projects")}>
          <Space>
            <IconArrowLeft />
            <span>Select a Project</span>
          </Space>
        </Button>
      </div>
    </Space>
  );
}
