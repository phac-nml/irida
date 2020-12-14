import React from "react";
import { Button, Popconfirm } from "antd";
import { red6 } from "../../../../styles/colors";
import {
  IconQuestionCircle,
  IconTrash,
} from "../../../../components/icons/Icons";

/**
 * Component to render a Delete announcement button.
 * @param {integer} id - the id of the announcement that is to be deleted.
 * @param {function} deleteAnnouncement - the function that deletes an announcement.
 * @returns {*}
 * @constructor
 */
export function DeleteAnnouncement({ id, deleteAnnouncement }) {
  return (
    <Popconfirm
      placement={"topRight"}
      title={i18n("DeleteAnnouncement.title")}
      onConfirm={() => deleteAnnouncement({ id })}
      okText={i18n("DeleteAnnouncement.ok")}
      icon={<IconQuestionCircle style={{ color: red6 }} />}
    >
      <Button shape={"circle"} className={"t-delete-announcement"}>
        <IconTrash />
      </Button>
    </Popconfirm>
  );
}
