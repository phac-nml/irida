import { AddNewButton } from "../Buttons/AddNewButton";
import React, { useContext, useEffect, useState } from "react";
import { Form, Modal, notification, Radio, Select, Typography } from "antd";
import {
  addMemberToProject,
  getAvailableUsersForProject,
} from "../../apis/projects/members";
import { useDebounce, useResetFormOnCloseModal } from "../../hooks";
import { SPACE_XS } from "../../styles/spacing";
import { PagedTableContext } from "../ant.design/PagedTable";

const { Option } = Select;
const { Text } = Typography;

export function AddMembersButton() {
  const ROLES = {
    PROJECT_USER: i18n("projectRole.PROJECT_USER"),
    PROJECT_OWNER: i18n("projectRole.PROJECT_OWNER"),
  };

  const { updateTable } = useContext(PagedTableContext);

  const [userId, setUserId] = useState();
  const [role, setRole] = useState(Object.keys(ROLES)[0]);
  const [visible, setVisible] = useState(false);
  const [query, setQuery] = useState("");
  const debouncedQuery = useDebounce(query, 350);
  const [results, setResults] = useState([]);
  const [form] = Form.useForm();
  useResetFormOnCloseModal({
    form,
    visible,
  });

  useEffect(() => {
    if (debouncedQuery) {
      getAvailableUsersForProject(debouncedQuery).then((data) =>
        setResults(data)
      );
    } else {
      setResults([]);
    }
  }, [debouncedQuery]);

  const options = results.map((u) => (
    <Option key={u.identifier}>
      <Text style={{ marginRight: SPACE_XS }}>{u.label}</Text>
      <Text type="secondary">{u.username}</Text>
    </Option>
  ));

  const addUserToProject = () => {
    addMemberToProject({ id: userId, role })
      .then((message) => {
        updateTable();
        notification.success({ message });
        setVisible(false);
      })
      .catch((message) => notification.error({ message }));
  };

  return (
    <>
      <AddNewButton
        onClick={() => setVisible(true)}
        text={i18n("AddMemberButton.label")}
      />
      <Modal
        visible={visible}
        onCancel={() => setVisible(false)}
        title={i18n("AddMemberButton.modal.title")}
        onOk={addUserToProject}
      >
        <Form layout="vertical" form={form}>
          <Form.Item
            label={"Search user by first name, last name, or username"}
          >
            <Select
              showSearch
              notFoundContent={null}
              onSearch={setQuery}
              onChange={setUserId}
              style={{ width: "100%" }}
              value={userId}
              filterOption={false}
            >
              {options}
            </Select>
          </Form.Item>
          <Form.Item label={"Project Role"}>
            <Radio.Group
              style={{ display: "flex" }}
              defaultValue={role}
              onChange={(e) => setRole(e.target.value)}
            >
              {Object.keys(ROLES).map((role) => (
                <Radio.Button key={role} value={role}>
                  {ROLES[role]}
                </Radio.Button>
              ))}
            </Radio.Group>
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}
