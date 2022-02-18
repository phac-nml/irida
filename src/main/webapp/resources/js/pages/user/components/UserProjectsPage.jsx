import React from "react";
import { useParams } from "react-router-dom";
import { notification, Space, Switch, Table, Typography } from "antd";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { formatDate } from "../../../utilities/date-utilities";
import { useUpdateProjectSubscriptionMutation } from "../../../apis/projects/project-subscriptions";
import { PagedTableProvider } from "../../../components/ant.design/PagedTable";
import {
  PagedTable,
  PagedTableContext,
} from "../../../components/ant.design/PagedTable";

/**
 * React component to display the user projects page.
 * @returns {*}
 * @constructor
 */
export default function UserProjectsPage() {
  const { userId } = useParams();
  const [updateProjectSubscription] = useUpdateProjectSubscriptionMutation();
  const columns = [
    {
      title: "ID",
      dataIndex: "projectId",
      key: "projectId",
    },
    {
      title: "Name",
      dataIndex: "projectName",
      key: "projectName",
      render: (text, record) => (
        <a href={setBaseUrl(`projects/${record.projectId}`)}>{text}</a>
      ),
    },
    {
      title: "Role",
      dataIndex: "roleName",
      key: "roleName",
      render: (text) => {
        if (text === "PROJECT_USER") return i18n("projectRole.PROJECT_USER");
        else if (text === "PROJECT_OWNER")
          return i18n("projectRole.PROJECT_OWNER");
      },
    },
    {
      title: "Date Added",
      dataIndex: "createdDate",
      key: "createdDate",
      render: (text) => formatDate({ date: text }),
    },
    {
      title: "Subscribed",
      dataIndex: "emailSubscribed",
      key: "emailSubscribed",
      render: (text, record) => <Switch defaultChecked={text} onChange={(checked) => updateSubscription(checked, record)} />,
    },
  ];

  function updateSubscription(checked, record) {
    console.log(
      "projectId = " +
        record.projectId +
        ", userId = " +
        userId +
        ", subscribe = " +
        checked
    );
    updateProjectSubscription({ id: record.id, subscribe: checked })
      .then((payload) => {
        notification.success({
          message: "SUCCESS",
        });
      })
      .catch((error) => {
        notification.error({
          message: "FAILED",
        });
      });
  };

  return (
    <Space direction="vertical">
      <Typography.Title level={4}>Projects</Typography.Title>
      <PagedTableProvider url={setBaseUrl(`/ajax/users/${userId}/projects/list`)} column="project.id" order="ascend">
        <PagedTable columns={columns} search={false} />
      </PagedTableProvider>
    </Space>
  );
}
