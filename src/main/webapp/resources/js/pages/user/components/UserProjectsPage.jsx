import React from "react";
import { useParams } from "react-router-dom";
import { notification, Row, Switch, Typography } from "antd";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { formatDate } from "../../../utilities/date-utilities";
import { useUpdateProjectSubscriptionMutation } from "../../../apis/projects/project-subscriptions";
import { PagedTableProvider } from "../../../components/ant.design/PagedTable";
import { PagedTable } from "../../../components/ant.design/PagedTable";

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
      title: i18n("UserProjectsPage.table.projectId"),
      dataIndex: "projectId",
      key: "projectId",
    },
    {
      title: i18n("UserProjectsPage.table.projectName"),
      dataIndex: "projectName",
      key: "projectName",
      render: (text, record) => (
        <a href={setBaseUrl(`projects/${record.projectId}`)}>{text}</a>
      ),
    },
    {
      title: i18n("UserProjectsPage.table.roleName"),
      dataIndex: "roleName",
      key: "roleName",
      render: (text) => {
        if (text === "PROJECT_USER") return i18n("projectRole.PROJECT_USER");
        else if (text === "PROJECT_OWNER")
          return i18n("projectRole.PROJECT_OWNER");
      },
    },
    {
      title: i18n("UserProjectsPage.table.createdDate"),
      dataIndex: "createdDate",
      key: "createdDate",
      render: (text) => formatDate({ date: text }),
    },
    {
      title: i18n("UserProjectsPage.table.emailSubscribed"),
      dataIndex: "emailSubscribed",
      key: "emailSubscribed",
      render: (text, record) => (
        <Switch
          defaultChecked={text}
          onChange={(checked) => updateSubscription(checked, record)}
        />
      ),
    },
  ];

  function updateSubscription(checked, record) {
    updateProjectSubscription({ id: record.id, subscribe: checked })
      .then(() => {
        notification.success({
          message: i18n("UserProjectsPage.notification.success"),
        });
      })
      .catch(() => {
        notification.error({
          message: i18n("UserProjectsPage.notification.error"),
        });
      });
  }

  return (
    <>
      <Row>
        <Typography.Title level={4}>
          {i18n("UserProjectsPage.title")}
        </Typography.Title>
      </Row>
      <Row>
        <PagedTableProvider
          url={setBaseUrl(`/ajax/subscriptions/${userId}/user/list`)}
          column="project.id"
          order="ascend"
        >
          <PagedTable columns={columns} search={false} />
        </PagedTableProvider>
      </Row>
    </>
  );
}
