import React, { useContext } from "react";
import { PagedTableContext } from "../../contexts/PagedTableContext";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { PagedTable } from "../../components/ant.design/PagedTable";
import { RemoteApiStatus } from "./RemoteApiStatus";

export function RemoteApiTable() {
  const { updateTable } = useContext(PagedTableContext);

  const columnDefs = [
    {
      title: i18n("iridaThing.id"),
      key: "id",
      dataIndex: "id"
    },
    {
      title: i18n("remoteapi.name"),
      key: "name",
      dataIndex: "name"
    },
    {
      title: i18n("iridaThing.timestamp"),
      key: "createdDate",
      dataIndex: "createdDate",
      render(text) {
        return formatInternationalizedDateTime(text);
      }
    },
    {
      title: i18n("remoteapi.status"),
      align: "right",
      width: 160,
      render(text, item) {
        return <RemoteApiStatus api={item} updateTable={updateTable} />;
      }
    }
  ];

  return <PagedTable columns={columnDefs} />;
}
