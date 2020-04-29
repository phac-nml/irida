import React, { useContext, useEffect, useState, useRef } from "react";
import {
  Button,
  Form,
  Modal,
  notification,
  Radio,
  Select,
  Typography,
} from "antd";
import { useDebounce, useResetFormOnCloseModal } from "../../hooks";
import {
  addUserGroupToProject,
  searchAvailableUserGroups,
} from "../../apis/projects/projects";
import { SPACE_XS } from "../../styles/spacing";
import { ProjectRolesContext } from "../../contexts/ProjectRolesContext";
import { PagedTableContext } from "../ant.design/PagedTable";

const { Option } = Select;
const { Text } = Typography;

export function AddGroupButton() {
  const { roles } = useContext(ProjectRolesContext);
  const { updateTable } = useContext(PagedTableContext);
  const groupRef = useRef();

  const [visible, setVisible] = useState(false);
  const [groupId, setGroupId] = useState();

  /*
  The value of the currently selected role from the role input
   */
  const [role, setRole] = useState("PROJECT_USER");

  /*
  Value to send to the server to query for a list of potential groups.
   */
  const [query, setQuery] = useState("");

  /*
  Since we don't want a post being send until the user is done typing, set a
  delay when to send the request based on the last typed letter.
   */
  const debouncedQuery = useDebounce(query, 350);

  /*
  List of groups to display to the user to select from.  Values returned from
  server from the debouncedQuery search.
   */
  const [results, setResults] = useState([]);

  /*
  Ant Design form
   */
  const [form] = Form.useForm();
  useResetFormOnCloseModal({
    form,
    visible,
  });

  /*
  Watch for changes to the forms visibility, when it becomes visible
  set keyboard focus onto the user name input.
   */
  useEffect(() => {
    if (visible) {
      setTimeout(() => groupRef.current.focus(), 100);
    }
  }, [visible]);

  /*
 Watch for changes to the debounced entered value for the user search.
 Once it changes send a request for filtered users.
  */
  useEffect(() => {
    if (debouncedQuery) {
      searchAvailableUserGroups({
        query: debouncedQuery,
        projectId: window.project.id,
      }).then((data) => setResults(data));
    } else {
      setResults([]);
    }
  }, [debouncedQuery]);

  const addGroupToProject = () =>
    addUserGroupToProject({
      projectId: window.project.id,
      role,
      groupId,
    }).then((message) => {
      updateTable();
      notification.success({ message });
      setVisible(false);
    });

  /*
  Before rendering format the results into Select.Option
   */
  const options = results.map((u) => (
    <Option className="t-new-group" key={u.key}>
      <Text style={{ marginRight: SPACE_XS }}>{u.name}</Text>
    </Option>
  ));

  return (
    <>
      <Button className="t-add-btn" onClick={() => setVisible(true)}>
        {i18n("AddGroupButton.label")}
      </Button>
      <Modal
        className="t-add-group-modal"
        title={i18n("AddGroupButton.label")}
        okButtonProps={{ disabled: typeof groupId === "undefined" }}
        visible={visible}
        onCancel={() => setVisible(false)}
        onOk={addGroupToProject}
        okText={i18n("AddGroupButton.modal.okText")}
      >
        <Form form={form} layout="vertical" initialValues={{ role }}>
          <Form.Item
            label={i18n("AddGroupButton.modal.group-label")}
            help={i18n("AddGroupButton.modal.group-help")}
            name="user"
          >
            <Select
              ref={groupRef}
              showSearch
              notFoundContent={null}
              onSearch={setQuery}
              onChange={setGroupId}
              value={groupId}
              style={{ width: "100%" }}
              filterOption={false}
            >
              {options}
            </Select>
          </Form.Item>
          <Form.Item label={i18n("AddGroupButton.modal.role")} name="role">
            <Radio.Group
              style={{ display: "flex" }}
              onChange={(e) => setRole(e.target.value)}
            >
              {roles.map((role) => (
                <Radio.Button key={role.value} value={role.value}>
                  {role.label}
                </Radio.Button>
              ))}
            </Radio.Group>
          </Form.Item>
        </Form>
      </Modal>
    </>
  );
}
