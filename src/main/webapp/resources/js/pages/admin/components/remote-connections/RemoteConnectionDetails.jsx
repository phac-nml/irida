import React, { useEffect, useState } from "react";
import { Button, Popconfirm, Space, Tabs, Typography } from "antd";
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
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    getConnectionDetails({ id: remoteId }).then(setDetails);
  }, [remoteId]);

  const dataSource = [
    {
      title: i18n("RemoteConnectionDetails.id"),
      desc: <span className="t-remote-id">{details.id}</span>,
    },
    {
      title: i18n("RemoteConnectionDetails.url"),
      desc: details.url,
    },
    {
      title: i18n("RemoteConnectionDetails.clientId"),
      desc: <span className="t-remote-clientId">{details.clientId}</span>,
    },
    {
      title: i18n("RemoteConnectionDetails.clientSecret"),
      desc: details.clientSecret,
    },
    {
      title: i18n("RemoteConnectionDetails.created"),
      desc: formatInternationalizedDateTime(details.created),
    },
  ];

  const removeConnection = () => {
    setDeleting(true);
    deleteRemoteApi({ id: remoteId }).then(returnToList);
  };

  const returnToList = () => navigate(setBaseUrl(`admin/remote_api`));

  return (
    <PageWrapper
      title={<span className="t-remote-name">{details.name}</span>}
      onBack={returnToList}
      headerExtras={<RemoteApiStatus key="status" api={{ id: remoteId }} />}
    >
      <Tabs tabPosition="left">
        <Tabs.TabPane
          tab={i18n("RemoteConnectionDetails.tab.details")}
          key="details"
        >
          <Title level={2}>
            {i18n("RemoteConnectionDetails.tab.details.title")}
          </Title>
          <BasicList dataSource={dataSource} />
        </Tabs.TabPane>
        <Tabs.TabPane
          tab={
            <span className="t-delete-tab">
              {i18n("RemoteConnectionDetails.tab.delete")}
            </span>
          }
          key="delete"
        >
          <Space direction="vertical" style={{ width: "100%" }}>
            <Title level={2}>
              {i18n("RemoteConnectionDetails.tab.delete.title")}
            </Title>
            <WarningAlert
              message={i18n("RemoteConnectionDetails.tab.delete.warning")}
            />
            <Popconfirm
              title={i18n("RemoteConnectionDetails.tab.delete.confirm")}
              placement="right"
              onConfirm={removeConnection}
              okButtonProps={{ className: "t-delete-confirm" }}
            >
              <Button
                className="t-delete-btn"
                type="primary"
                danger
                loading={deleting}
              >
                {i18n("RemoteConnectionDetails.tab.delete.button")}
              </Button>
            </Popconfirm>
          </Space>
        </Tabs.TabPane>
      </Tabs>
    </PageWrapper>
  );
}
