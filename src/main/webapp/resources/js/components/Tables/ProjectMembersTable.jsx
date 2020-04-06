import React, { useContext, useState } from "react";
import { PagedTable, PagedTableContext } from "../ant.design/PagedTable";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { Button, notification, Popconfirm, Select, Tooltip } from "antd";
import { IconRemove } from "../icons/Icons";
import { setBaseUrl } from "../../utilities/url-utilities";
import {
  removeUserFromProject,
  updateUserRoleOnProject
} from "../../apis/projects/members";

function ProjectRoleSelect({ user }) {
  const [role, setRole] = useState(user.role);
  const [loading, setLoading] = useState(false);

  const ROLES = {
    PROJECT_USER: i18n("projectRole.PROJECT_USER"),
    PROJECT_OWNER: i18n("projectRole.PROJECT_OWNER")
  };

  const onChange = value => {
    setLoading(true);
    updateUserRoleOnProject({
      id: user.id,
      role: value
    })
      .then(message => {
        notification.success({ message });
        setRole(value);
      })
      .catch(error => {
        console.log(error);
        notification.error({
          message: error.response.data
        });
      })
      .then(() => setLoading(false));
  };

  return (
    <Select
      value={role}
      style={{ width: "100%" }}
      onChange={onChange}
      loading={loading}
      disabled={loading || user.id === window.PAGE.user}
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
    if (user.id !== window.PAGE.user) {
      notification.success({ message });
      updateTable();
    } else {
      // If the user can remove themselves from the project, then when they
      // are removed redirect them to their project page since they cannot
      // use this project anymore.
      window.location.href = setBaseUrl(`/projects`);
    }
  };

  const onConfirm = () => {
    setLoading(true);
    removeUserFromProject(user.id)
      .then(removeSuccess)
      .catch(error =>
        notification.error({
          message: error.response.data
        })
      )
      .finally(() => setLoading(false));
  };

  return (
    <Popconfirm
      onConfirm={onConfirm}
      placement="topLeft"
      title={i18n("RemoveMemberButton.confirm")}
    >
      <Tooltip title={i18n("RemoveMemberButton.tooltip")} placement="left">
        <Button
          icon={<IconRemove />}
          shape="circle-outline"
          loading={loading}
        />
      </Tooltip>
    </Popconfirm>
  );
}

export function ProjectMembersTable() {
  const { updateTable } = useContext(PagedTableContext);

  const columns = [
    {
      title: i18n("ProjectMembersTable.name"),
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
      title: i18n("ProjectMembersTable.since"),
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
