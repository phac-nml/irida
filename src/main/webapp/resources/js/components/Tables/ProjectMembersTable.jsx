import React, { useContext, useEffect, useState } from "react";
import { PagedTable, PagedTableContext } from "../ant.design/PagedTable";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { Button, notification, Popconfirm, Select } from "antd";
import { IconTrash } from "../icons/Icons";
import { setBaseUrl } from "../../utilities/url-utilities";
import { removeUserFromProject } from "../../apis/projects/members";
import { showNotification } from "../../modules/notifications";

function ProjectRoleSelect({ user }) {
  const [role, setRole] = useState(user.role);
  const [loading, setLoading] = useState(false);

  const ROLES = {
    PROJECT_USER: i18n("projectRole.PROJECT_USER"),
    PROJECT_OWNER: i18n("projectRole.PROJECT_OWNER")
  };

  const onChange = value => {
    setLoading(true);
    setRole(value);
  };

  return (
    <Select
      defaultValue={role}
      style={{ width: "100%" }}
      onChange={onChange}
      loading={loading}
    >
      {Object.keys(ROLES).map(key => (
        <Select.Option value={key} key={key}>
          {ROLES[key]}
        </Select.Option>
      ))}
    </Select>
  );
}

function RemoveMemberButton({ user, updateTable }) {
  const [loading, setLoading] = useState(false);

  const removeSuccess = message => {
    notification.success({ message });
    updateTable();
  };

  const onConfirm = () => {
    setLoading(true);
    removeUserFromProject(user.id)
      .then(removeSuccess)
      .catch(response =>
        notification.error({ message: i18n("RemoveMemberButton.error") })
      )
      .finally(() => setLoading(false));
  };

  return (
    <Popconfirm
      onConfirm={onConfirm}
      placement="topLeft"
      title={i18n("RemoveMemberButton.confirm")}
    >
      <Button icon={<IconTrash />} shape="circle-outline" loading={loading} />
    </Popconfirm>
  );
}

export function ProjectMembersTable() {
  const { updateTable } = useContext(PagedTableContext);

  const columns = [
    {
      title: i18n("project.table.collaborator.name"),
      dataIndex: "name",
      render(text, item) {
        return <a href={setBaseUrl(`/users/${item.id}`)}>{text}</a>;
      }
    },
    {
      title: i18n("project.table.collaborator.role"),
      dataIndex: "role",
      render(text, item) {
        return <ProjectRoleSelect user={item} />;
      }
    },
    {
      title: i18n("project.table.collaborator.since"),
      dataIndex: "createdDate",
      render(text) {
        return formatInternationalizedDateTime(text);
      }
    }
  ];

  if (window.PAGE.canManage) {
    columns.push({
      align: "right",
      render(text, item) {
        return <RemoveMemberButton user={item} updateTable={updateTable} />;
      }
    });
  }

  return <PagedTable columns={columns} />;
}
