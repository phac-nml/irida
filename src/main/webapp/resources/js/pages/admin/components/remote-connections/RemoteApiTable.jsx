import React, { useContext } from "react";
import {
  PagedTable,
  PagedTableContext,
} from "../../../../components/ant.design/PagedTable";
import { Button, Popconfirm, Space, Typography } from "antd";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import { isAdmin } from "../../../../utilities/role-utilities";
import { RemoteApiStatus } from "./RemoteApiStatus";
import { deleteRemoteApi } from "../../../../apis/remote-api/remote-api";

/**
 * Render a table to display remote API's
 * @returns {string|*}
 * @constructor
 */
export function RemoteApiTable() {
  const { updateTable } = useContext(PagedTableContext);

  const removeConnection = (id) => {
    deleteRemoteApi({ id }).then(updateTable);
  };

  const admin = isAdmin();

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
      width: 430,
      render(text, item) {
        return (
          <Space>
            <RemoteApiStatus api={item} updateTable={updateTable} />
            {admin && (
              <Popconfirm
                title={i18n("RemoteApi.delete-confirm")}
                placement="topRight"
                onConfirm={() => removeConnection(item.id)}
                okButtonProps={{ className: "t-delete-confirm" }}
              >
                <Button size="small" type="link">
                  {i18n("RemoteApi.delete")}
                </Button>
              </Popconfirm>
            )}
          </Space>
        );
      },
    },
  ];

  if (admin) {
    columnDefs.splice(
      2,
      0,
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
      }
    );
  }

  return <PagedTable className="t-remoteapi-table" columns={columnDefs} />;
}
