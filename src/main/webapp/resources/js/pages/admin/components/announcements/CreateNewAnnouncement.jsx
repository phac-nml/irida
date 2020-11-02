import React, { useRef } from "react";
import { Modal } from "antd";
import { AddNewButton } from "../../../../components/Buttons/AddNewButton";
import { MarkdownEditor } from "../../../../components/markdown/MarkdownEditor";
import { FONT_COLOR_PRIMARY } from "../../../../styles/fonts";
import { IconEdit } from "../../../../components/icons/Icons";

/**
 * Component to add a button which will open a modal to create an announcement.
 * @param {function} createAnnouncement
 * @returns {*}
 * @constructor
 */
export function CreateNewAnnouncement({ createAnnouncement }) {
  const markdownRef = useRef();

  function saveMarkdown() {
    const md = markdownRef.current.getMarkdown();
    createAnnouncement(md);
  }

  function displayModal() {
    Modal.confirm({
      title: i18n("CreateNewAnnouncement.title"),
      icon: <IconEdit style={{ color: FONT_COLOR_PRIMARY }} />,
      width: "80%",
      content: <MarkdownEditor ref={markdownRef} />,
      okText: i18n("CreateNewAnnouncement.okBtn"),
      okButtonProps: {
        className: "t-submit-announcement"
      },
      onOk() {
        saveMarkdown();
      }
    });
  }

  return (
    <AddNewButton
      className="t-create-announcement"
      onClick={displayModal}
      text={i18n("CreateNewAnnouncement.title")}
    />
  );
}
