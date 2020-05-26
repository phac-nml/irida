import React, { useEffect, useRef, useState } from "react";
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
import { useRoles } from "../../contexts/roles-context";
import {
  addUserGroupToProject,
  getAvailableGroupsForProject,
} from "../../apis/projects/user-groups";
import { SPACE_XS } from "../../styles/spacing";

const { Option } = Select;
const { Text } = Typography;

/**
 * React component to add render a Button to add a user group to a project.
 * @param {string} defaultRole button default
 * @param {function} onGroupAdded what to do after the group is added
 * @returns {*}
 * @constructor
 */
export function AddGroupButton({ defaultRole, onGroupAdded = () => {} }) {
  /*
  Required a reference to the user select input so that focus can be set
  to it when the window opens.
   */
  const groupRef = useRef();

  /*
  Get a list of project roles
   */
  const { roles } = useRoles();

  /*
  Whether the modal to add a user is visible
   */
  const [visible, setVisible] = useState(false);

  /*
  The identifier for the currently selected user from the user input
   */
  const [groupId, setGroupId] = useState(undefined);

  /*
  The value of the currently selected role from the role input
   */
  const [role, setRole] = useState(defaultRole);

  /*
  Value to send to the server to query for a list of potential users.
   */
  const [query, setQuery] = useState("");

  /*
  Since we don't want a post being send until the user is done typing, set a
  delay when to send the request based on the last typed letter.
   */
  const debouncedQuery = useDebounce(query, 350);

  /*
  List of users to display to the user to select from.  Values returned from
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
  Watch for changes to the debounced entered value for the user search.
  Once it changes send a request for filtered users.
   */
  useEffect(() => {
    if (debouncedQuery) {
      getAvailableGroupsForProject(debouncedQuery).then((data) =>
        setResults(data)
      );
    } else {
      setResults([]);
    }
  }, [debouncedQuery]);

  /*
  Watch for changes to the forms visibility, when it becomes visible
  set keyboard focus onto the user name input.
   */
  useEffect(() => {
    if (visible) {
      form.resetFields();
      setTimeout(() => groupRef.current.focus(), 100);
    }
  }, [form, visible]);

  /*
  Add the user group
   */
  const addUserGroup = () => {
    addUserGroupToProject({ groupId, role }).then((message) => {
      onGroupAdded();
      notification.success({ message });
      form.resetFields();
      setVisible(false);
    });
  };

  /*
  Before rendering format the results into Select.Option
   */
  const options = results.map((u) => (
    <Option className="t-new-member" key={u.identifier}>
      <Text style={{ marginRight: SPACE_XS }}>{u.label}</Text>
    </Option>
  ));

  return (
    <>
      <Button onClick={() => setVisible(true)}>
        {i18n("AddGroupButton.label")}
      </Button>
      <Modal
        onCancel={() => setVisible(false)}
        visible={visible}
        onOk={addUserGroup}
        okText={i18n("AddGroupButton.group.okText")}
      >
        <Form form={form} layout="vertical" initialValues={{ role }}>
          <Form.Item
            label={i18n("AddGroupButton.group.label")}
            help={i18n("AddGroupButton.group.label-help")}
            name="user"
          >
            <Select
              ref={groupRef}
              showSearch
              notFoundContent={null}
              onSearch={setQuery}
              onChange={setGroupId}
              style={{ width: "100%" }}
              value={groupId}
              filterOption={false}
            >
              {options}
            </Select>
          </Form.Item>
          <Form.Item label={i18n("AddGroupButton.group.role")} name="role">
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
