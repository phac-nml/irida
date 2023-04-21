import {
  Button,
  Form,
  Modal,
  notification,
  Radio,
  Select,
  Typography,
} from "antd";
import React, { useEffect, useRef, useState } from "react";
import {
  addUserGroupToProject,
  getAvailableGroupsForProject,
} from "../../apis/projects/user-groups";
import { useMetadataRoles } from "../../contexts/metadata-roles-context";
import { useProjectRoles } from "../../contexts/project-roles-context";
import { useDebounce, useResetFormOnCloseModal } from "../../hooks";
import { SPACE_XS } from "../../styles/spacing";

const { Option } = Select;
const { Text } = Typography;

/**
 * React component to add render a Button to add a user group to a project.
 *
 * @param {string} defaultRole - button default
 * @param {function} onGroupAdded - what to do after the group is added
 * @param {number} projectId - identifier for the project to add the group to.
 * @returns {*}
 * @constructor
 */
export function AddGroupButton({
  defaultRole,
  onGroupAdded = () => Function.prototype,
  projectId,
}) {
  const { roles: metadataRoles } = useMetadataRoles();
  const [metadataRole, setMetadataRole] = useState("LEVEL_1");

  /*
  Required a reference to the user select input so that focus can be set
  to it when the window opens.
   */
  const groupRef = useRef();

  /*
  Get a list of project roles
   */
  const { roles } = useProjectRoles();

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
      getAvailableGroupsForProject({
        projectId,
        query: debouncedQuery,
      }).then((data) => setResults(data));
    } else {
      setResults([]);
    }
  }, [debouncedQuery, projectId]);

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
    addUserGroupToProject({
      projectId,
      groupId,
      role,
      metadataRole,
    })
      .then((message) => {
        onGroupAdded();
        notification.success({ message });
        setVisible(false);
        setQuery("");
        setGroupId(undefined);
        setRole(defaultRole);
        setMetadataRole("LEVEL_1");
        form.resetFields();
      })
      .catch((message) => {
        notification.error({ message });
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
      <Button className="t-add-user-group-btn" onClick={() => setVisible(true)}>
        {i18n("AddGroupButton.label")}
      </Button>
      <Modal
        className="t-add-user-group-modal"
        onCancel={() => setVisible(false)}
        visible={visible}
        onOk={addUserGroup}
        okText={i18n("AddGroupButton.group.okText")}
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{
            groupId,
            role,
            metadataRole,
          }}
        >
          <Form.Item
            label={i18n("AddGroupButton.group.label")}
            help={i18n("AddGroupButton.group.label-help")}
            name="groupId"
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
              onChange={(e) => {
                setRole(e.target.value);
                if (e.target.value === "PROJECT_OWNER") {
                  setMetadataRole("LEVEL_4");
                }
              }}
            >
              {roles.map((role) => (
                <Radio.Button key={role.value} value={role.value}>
                  {role.label}
                </Radio.Button>
              ))}
            </Radio.Group>
          </Form.Item>
          <Form.Item
            label={i18n("AddMemberButton.modal.metadataRole")}
            name="metadataRole"
          >
            <Radio.Group
              style={{ display: "flex" }}
              onChange={(e) => setMetadataRole(e.target.value)}
              disabled={role === "PROJECT_OWNER"}
            >
              {metadataRoles.map((role) => (
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
