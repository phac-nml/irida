import React, { useEffect, useState } from "react";
import { Button, PageHeader, Popconfirm, Space, Tabs, Typography } from "antd";
import { RemoteApiStatus } from "./RemoteApiStatus";
import {
  deleteRemoteApi,
  getConnectionDetails,
} from "../../../../apis/remote-api/remote-api";
import { BasicList } from "../../../../components/lists";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { WarningAlert } from "../../../../components/alerts";
import { PageWrapper } from "../../../../components/page/PageWrapper";
import { navigate } from "@reach/router";

const { Title } = Typography;

export default function RemoteConnectionDetails({ remoteId }) {
  const [details, setDetails] = useState({});

  useEffect(() => {
    getConnectionDetails({ id: remoteId }).then(setDetails);
  }, []);

  const dataSource = [
    {
      title: "ID",
      desc: details.id,
    },
    {
      title: "SERVICE URL",
      desc: details.url,
    },
    {
      title: "CLIENT ID",
      desc: details.clientId,
    },
    {
      title: "CLIENT SECRET",
      desc: details.clientSecret,
    },
    {
      title: "CREATED",
      desc: formatInternationalizedDateTime(details.created),
    },
  ];

  const removeConnection = () =>
    deleteRemoteApi({ id: remoteId }).then(returnToList);

  const returnToList = () => navigate(setBaseUrl(`admin/remote_api`));

  return (
    <PageWrapper
      title={details.name}
      onBack={returnToList}
      headerExtras={<RemoteApiStatus key="status" api={{ id: remoteId }} />}
    >
      <Tabs tabPosition="left">
        <Tabs.TabPane tab={"DETAILS"} key="details">
          <Title level={2}>Connection Details</Title>
          <BasicList dataSource={dataSource} />
        </Tabs.TabPane>
        <Tabs.TabPane tab={"DELETE CONNECTION"} key="delete">
          <Space direction="vertical" style={{ width: "100%" }}>
            <Title level={2}>Delete Connection</Title>
            <WarningAlert message="Warning! Deletion of a connection is a permanent action!" />
            <Popconfirm
              title={"Delete connection?"}
              placement="right"
              onConfirm={removeConnection}
            >
              <Button type="primary" danger>
                DELETE
              </Button>
            </Popconfirm>
          </Space>
        </Tabs.TabPane>
      </Tabs>
    </PageWrapper>
  );
}
