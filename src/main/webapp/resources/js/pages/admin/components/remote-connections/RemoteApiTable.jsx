import React, { useContext } from "react";
import {
  PagedTable,
  PagedTableContext,
} from "../../../../components/ant.design/PagedTable";
import { Button, Popconfirm, Space, Typography } from "antd";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import { isAdmin } from "../../../../utilities/role-utilities";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { RemoteApiStatus } from "./RemoteApiStatus";

/**
 * Render a table to display remote API's
 * @returns {string|*}
 * @constructor
 */
export function RemoteApiTable() {
  const { updateTable } = useContext(PagedTableContext);

  const removeConnection = () => {
    deleteRemoteApi({ id: params.remoteId }).then(updateTable);
  };

  const columnDefs = [
    {
      title: i18n("RemoteApi.name"),
      key: "name",
      dataIndex: "name",
      render(text) {
        return <span className="t-api-name">{text}</span>;
      },
    },
    {
      title: i18n("RemoteApi.serviceurl"),
      key: "serviceURI",
      dataIndex: "serviceURI",
    },
    {
      title: i18n("RemoteApi.clientid"),
      key: "clientId",
      dataIndex: "clientId",
      render(text) {
        return <Typography.Text copyable>{text}</Typography.Text>;
      },
    },
    {
      title: i18n("RemoteApi.secret"),
      key: "clientSecret",
      dataIndex: "clientSecret",
      render(text) {
        return <Typography.Text copyable>{text}</Typography.Text>;
      },
    },
    {
      title: i18n("iridaThing.timestamp"),
      key: "createdDate",
      dataIndex: "createdDate",
      render(text) {
        return formatInternationalizedDateTime(text);
      },
    },
    {
      title: "",
      align: "right",
      fixed: "right",
      width: 400,
      render(text, item) {
        return (
          <Space>
            <RemoteApiStatus api={item} updateTable={updateTable} />
            {isAdmin() && (
              <Popconfirm
                title={i18n("RemoteConnectionDetails.tab.delete.confirm")}
                placement="topRight"
                onConfirm={removeConnection}
                okButtonProps={{ className: "t-delete-confirm" }}
              >
                <Button type="text">{i18n("RemoteApi.delete")}</Button>
              </Popconfirm>
            )}
          </Space>
        );
      },
    },
  ];

  return <PagedTable className="t-remoteapi-table" columns={columnDefs} />;
}
