import React, { useState } from "react";
import { Button, notification, Popconfirm, Tooltip } from "antd";
import { setBaseUrl } from "../../utilities/url-utilities";
import { removeUserFromProject } from "../../apis/projects/members";
import { IconRemove } from "../icons/Icons";

export function RemoveMemberButton({ user, updateTable }) {
  const [loading, setLoading] = useState(false);

  const removeSuccess = (message) => {
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
      .catch((error) =>
        notification.error({
          message: error.response.data,
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
