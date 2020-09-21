import React, { useContext } from "react";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import {
  PagedTable,
  PagedTableContext,
} from "../../../../components/ant.design/PagedTable";
import { RemoteApiStatus } from "./RemoteApiStatus";
import { Link } from "@reach/router";

/**
 * Render a table to display remote API's
 * @returns {string|*}
 * @constructor
 */
export function RemoteApiTable() {
  const { updateTable } = useContext(PagedTableContext);

  const columnDefs = [
    {
      title: i18n("remoteapi.name"),
      key: "name",
      dataIndex: "name",
      render(text, record) {
        return (
          <Link
            to={setBaseUrl(`/admin/remote_api/${record.id}`)}
            className="t-api-name"
          >
            {text}
          </Link>
        );
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
      title: i18n("remoteapi.status"),
      align: "right",
      width: 330,
      render(text, item) {
        return <RemoteApiStatus api={item} updateTable={updateTable} />;
      },
    },
  ];

  return <PagedTable className="t-remoteapi-table" columns={columnDefs} />;
}
