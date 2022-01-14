import React, { useContext } from "react";
import {
  PagedTable,
  PagedTableContext,
} from "../../../../components/ant.design/PagedTable";
import {
  formatInternationalizedDateTime
} from "../../../../utilities/date-utilities";
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

  const columnDefs = [
    {
      title: i18n("remoteapi.name"),
      key: "name",
      dataIndex: "name",
      render(text, record) {
        return isAdmin() ? (
          <a
            href={setBaseUrl(`/admin/remote_api/${record.id}`)}
            className="t-api-name"
          >
            {text}
          </a>
        ) : (
          <span className="t-api-name">{text}</span>
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
