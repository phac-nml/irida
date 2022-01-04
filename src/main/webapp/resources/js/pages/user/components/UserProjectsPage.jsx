import React from "react";
import { useParams } from "react-router-dom";
import {
  Space,
  Table,
  Typography,
} from "antd";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { formatDate } from "../../../utilities/date-utilities";
import { useGetUserProjectDetailsQuery } from "../../../apis/users/users";

/**
 * React component to display the user projects page.
 * @returns {*}
 * @constructor
 */
export default function UserProjectsPage() {
  const { userId } = useParams();
  const { data, isSuccess } = useGetUserProjectDetailsQuery(userId);
  const columns = [
    {
      title: 'ID',
      dataIndex: 'projectId',
      key: 'projectId',
    },
    {
      title: 'Name',
      dataIndex: 'projectName',
      key: 'projectName',
      render: (text, record) => <a href={setBaseUrl(`projects/${record.projectId}`)}>{text}</a>
    },
    {
      title: 'Role',
      dataIndex: 'roleName',
      key: 'roleName',
      render: (text) => {
        if(text === "PROJECT_USER")
          return i18n("projectRole.PROJECT_USER")
        else if (text === "PROJECT_OWNER")
          return i18n("projectRole.PROJECT_OWNER")
      }
    },
    {
      title: 'Date Added',
      dataIndex: 'createdDate',
      key: 'createdDate',
      render: ((text) => formatDate({ date: text }))
    },
    {
      title: 'Subscribed',
    }
  ];

  if (isSuccess) {
    return (
      <Space direction="vertical">
        <Typography.Title level={4}>Projects</Typography.Title>
        <Table dataSource={data.projects} columns={columns} rowKey={(row) => row.rowKey} />
      </Space>
    );
  } else {
    return null;
  }
}
