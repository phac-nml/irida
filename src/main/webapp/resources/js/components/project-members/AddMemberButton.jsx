import { AddNewButton } from "../Buttons/AddNewButton";
import React, { useState, useEffect } from "react";
import { Modal, Select } from "antd";
import { getAvailableUsersForProject } from "../../apis/projects/members";
import { useDebounce } from "../../hooks";

export function AddMembersButton() {
  const [visible, setVisible] = useState(false);
  const [query, setQuery] = useState("");
  const debouncedQuery = useDebounce(query, 300);

  useEffect(() => {
    setQuery(debouncedQuery);
    getAvailableUsersForProject(debouncedQuery);
  }, [debouncedQuery]);

  return (
    <>
      <AddNewButton
        onClick={() => setVisible(true)}
        text={i18n("project.members.edit.add")}
      />
      <Modal visible={visible} onCancel={() => setVisible(false)}>
        <Select
          showSearch
          bordered
          onChange={setQuery}
          style={{ width: "100%" }}
        />
      </Modal>
    </>
  );
}
