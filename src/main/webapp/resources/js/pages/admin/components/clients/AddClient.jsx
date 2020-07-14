import React, { useRef } from "react";
import { Modal } from "antd";
import { IconEdit } from "../../../../components/icons/Icons";
import { FONT_COLOR_PRIMARY } from "../../../../styles/fonts";
import { MarkdownEditor } from "../../../../components/markdown/MarkdownEditor";
import { AddNewButton } from "../../../../components/Buttons/AddNewButton";
import { setBaseUrl } from "../../../../utilities/url-utilities";

/**
 * Component to add a button which will open a modal to add a client.
 * @returns {*}
 * @constructor
 */
export function AddClient() {

  function displayModal() {
    Modal.confirm({
      title: i18n("CreateNewAnnouncement.title"),
      icon: <IconEdit style={{color: FONT_COLOR_PRIMARY}}/>,
      width: "80%",
      content: <MarkdownEditor ref={markdownRef}/>,
      okText: i18n("CreateNewAnnouncement.okBtn"),
      okButtonProps: {
        className: "t-submit-announcement"
      },
      onOk() {

      }
    });
  }

  return (
    <AddNewButton
      className={"t-add-client-btn"}
      onClick={displayModal}
      text={i18n("clients.add")}
    />
  );
}