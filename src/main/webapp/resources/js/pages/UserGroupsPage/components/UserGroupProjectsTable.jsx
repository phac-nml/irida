import React from "react";
import { getProjectsForUserGroup } from "../../../apis/users/groups";
import { Button, Table } from "antd";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { getPaginationOptions } from "../../../utilities/antdesign-table-utilities";

/**
 * Display a table of projects the current user group is used on.
 * @param {number} groupId identifier for the current user group
 * @returns {string|*}
 * @constructor
 */
export function UserGroupProjectsTable({ groupId }) {
  const [projects, setProjects] = React.useState();
  const [total, setTotal] = React.useState(0);

  const columns = [
    {
      dataIndex: "name",
      title: i18n("UserGroupProjectTable.name"),
      render(text, item) {
        return (
          <Button href={setBaseUrl(`/projects/${item.id}`)} type="link">
            {text}
          </Button>
        );
      },
    },
    { dataIndex: "role", title: i18n("UserGroupProjectTable.role") },
    {
      title: i18n("UserGroupProjectTable.create"),
      dataIndex: "createdDate",
      render(text) {
        return formatInternationalizedDateTime(text);
      },
    },
  ];

  React.useEffect(() => {
    getProjectsForUserGroup(groupId).then((data) => {
      setProjects(data);
      setTotal(data.length);
    });
  }, [groupId]);

  return (
    <Table
      columns={columns}
      dataSource={projects}
      pagination={getPaginationOptions(total)}
    />
  );
}
