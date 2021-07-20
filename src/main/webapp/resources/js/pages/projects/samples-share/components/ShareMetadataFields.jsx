import { Button, Form, Popover, Select, Space, Table, Tag } from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import {
  useGetMetadataFieldsForProjectQuery,
  useGetMetadataRestrictionsQuery,
} from "../../../../apis/metadata/field";
import { IconArrowLeft } from "../../../../components/icons/Icons";
import {
  setFields,
  setPreviousStep,
  updateFields,
} from "../services/shareSlice";

export function ShareMetadataFields({ projectId }) {
  const dispatch = useDispatch();
  const { target = {}, fields = [], samples, locked } = useSelector(
    (state) => state.reducer
  );
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
    data: targetFields,
    isLoading: targetLoading,
  } = useGetMetadataFieldsForProjectQuery(target.identifier, {
    skip: target.identifier === undefined,
  });

  const getPermissionStatus = (current, target) => {
    const currentRestriction = restrictions.findIndex(
      (restriction) => restriction.value === item.current.restriction
    );
    const targetRestriction = restrictions.findIndex(
      (restriction) => restriction.value === item.target.restriction
    );

    return currentRestriction - targetRestriction;
  };

  React.useEffect(() => {
    if (!currentLoading && !targetLoading) {
      const merged = currentFields.map((current) => {
        const target = targetFields?.find(
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
  }, [targetFields, targetLoading, currentFields, currentLoading]);

  const updateRestriction = (index, value) => {
    dispatch(updateFields(index, value));
  };

  const renderRestriction = (text, item, index) => {
    const current = restrictions.findIndex(
      (restriction) => restriction.value === item.current.restriction
    );
    const target = restrictions.findIndex(
      (restriction) => restriction.value === item.target.restriction
    );

    const loweredPermission = current > target;
    const higherPermission = current < target;

    const feedback = {
      hasFeedback: true,
      validateStatus: loweredPermission ? "warning" : "success",
      help: loweredPermission
        ? "Permissions are less secure in the target project"
        : higherPermission
        ? "Permissions are more secure in the target project"
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

  const copyToTarget = () => {
    console.log({ samples, locked, fields, target });
  };

  return (
    <Space direction="vertical" style={{ display: "block" }}>
      <Form>
        <Table
          loading={currentLoading && targetLoading}
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
                    {!item.target.exists && (
                      <Popover
                        placement={"right"}
                        content={"This field do exist in the target project."}
                      >
                        <Tag color="green">NEW</Tag>
                      </Popover>
                    )}
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
              title: "target Project Restrictions",
              dataIndex: ["target", "restriction"],
              render: renderRestriction,
              width: 500,
            },
          ]}
          dataSource={fields}
        />
      </Form>
      <div style={{ display: "flex", justifyContent: "space-between" }}>
        <Button onClick={() => dispatch(setPreviousStep())}>
          <Space>
            <IconArrowLeft />
            <span>Review Samples</span>
          </Space>
        </Button>
        <Button type="primary" onClick={copyToTarget}>
          <Space>
            <span>Copy Samples</span>
          </Space>
        </Button>
      </div>
    </Space>
  );
}
