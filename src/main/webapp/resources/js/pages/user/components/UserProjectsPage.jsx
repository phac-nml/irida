import React from "react";
import { useParams } from "react-router-dom";
import { notification, Switch, Typography } from "antd";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { formatDate } from "../../../utilities/date-utilities";
import { useUpdateProjectSubscriptionMutation } from "../../../apis/projects/project-subscriptions";
import {
  PagedTable,
  PagedTableProvider,
} from "../../../components/ant.design/PagedTable";
import { LinkButton } from "../../../components/Buttons/LinkButton";

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
      className: "t-projectId",
    },
    {
      title: i18n("UserProjectsPage.table.projectName"),
      dataIndex: "projectName",
      key: "projectName",
      render: (text, record) => (
        <LinkButton
          text={
            <Typography.Text
              style={{ width: 100 }}
              ellipsis={{ tooltip: true }}
            >
              {text}
            </Typography.Text>
          }
          href={setBaseUrl(`projects/${record.projectId}`)}
        />
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
          className="t-emailSubscribed"
        />
      ),
    },
  ];

  function updateSubscription(checked, record) {
    updateProjectSubscription({ id: record.id, subscribe: checked })
      .then((response) => {
        notification.success({ message: response.data.message });
      })
      .catch((error) => {
        notification.error({ message: error.response.data.error });
      });
  }

  return (
    <>
      <Typography.Title level={4}>
        {i18n("UserProjectsPage.title")}
      </Typography.Title>
      <PagedTableProvider
        url={setBaseUrl(`/ajax/subscriptions/${userId}/user/list`)}
        column="project.id"
        order="ascend"
      >
        <PagedTable columns={columns} search={false} />
      </PagedTableProvider>
    </>
  );
}
