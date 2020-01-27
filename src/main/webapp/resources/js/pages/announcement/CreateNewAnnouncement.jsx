import React, { useRef } from "react";
import { Modal } from "antd";
import { AddNewButton } from "../../components/Buttons/AddNewButton";
import { MarkdownEditor } from "../../components/editors/MarkdownEditor";
import { EditOutlined } from "@ant-design/icons";
import { FONT_COLOR_PRIMARY } from "../../styles/fonts";

export function CreateNewAnnouncement({ createAnnouncement }) {
  const markdownRef = useRef();

  function saveMarkdown() {
    const md = markdownRef.current.getMarkdown();
    createAnnouncement(md);
  }

  function displayModal() {
    Modal.confirm({
      title: i18n("CreateNewAnnouncement.title"),
      icon: <EditOutlined style={{ color: FONT_COLOR_PRIMARY }} />,
      width: "80%",
      content: <MarkdownEditor ref={markdownRef} />,
      okText: i18n("CreateNewAnnouncement.okBtn"),
      onOk() {
        saveMarkdown();
      }
    });
  }

  return (
    <AddNewButton
      onClick={displayModal}
      text={i18n("CreateNewAnnouncement.title")}
    />
  );
}
