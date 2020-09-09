import React, { useRef } from "react";
import { Button, Modal } from "antd";
import { MarkdownEditor } from "../../../../components/markdown/MarkdownEditor";
import { FONT_COLOR_PRIMARY } from "../../../../styles/fonts";
import { IconEdit } from "../../../../components/icons/Icons";

/**
 * Render React component to edit an announcement.
 * @param {string} announcement - announcement to edit.
 * @param {function} updateAnnouncement - function to update the announcement.
 * @returns {*}
 * @constructor
 */
export function EditAnnouncement({ announcement, updateAnnouncement }) {
  const markdownRef = useRef();

  function saveMarkdown() {
    const md = markdownRef.current.getMarkdown();
    updateAnnouncement({ message: md, id: announcement.id });
  }

  function displayModal() {
    Modal.confirm({
      title: i18n("EditAnnouncement.title"),
      icon: <IconEdit style={{ color: FONT_COLOR_PRIMARY }} />,
      width: "80%",
      content: (
        <MarkdownEditor ref={markdownRef} markdown={announcement.message} />
      ),
      okText: i18n("EditAnnouncement.okBtn"),
      onOk() {
        saveMarkdown();
      }
    });
  }

  return (
    <Button shape={"circle"} onClick={displayModal}>
      <IconEdit />
    </Button>
  );
}
