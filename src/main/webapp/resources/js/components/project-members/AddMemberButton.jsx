import React, { useContext, useEffect, useRef, useState } from "react";
import {
  Button,
  Form,
  Modal,
  notification,
  Radio,
  Select,
  Typography,
} from "antd";
import {
  addMemberToProject,
  getAvailableUsersForProject,
} from "../../apis/projects/members";
import { useDebounce, useResetFormOnCloseModal } from "../../hooks";
import { SPACE_XS } from "../../styles/spacing";
import { PagedTableContext } from "../ant.design/PagedTable";
import { ProjectRolesContext } from "../../contexts/ProjectRolesContext";

const { Option } = Select;
const { Text } = Typography;

/**
 * React component that adds a button to open a modal to add a new member
 * to a project.  User can search for a user by first name, last name or
 * username, and select the project role for the user.
 * @returns {*}
 * @constructor
 */
export function AddMembersButton() {
  /*
  Required a reference to the user select input so that focus can be set
  to it when the window opens.
   */
  const userRef = useRef();
  const { roles } = useContext(ProjectRolesContext);
  const { updateTable } = useContext(PagedTableContext);

  /*
  Whether the modal to add a user is visible
   */
  const [visible, setVisible] = useState(false);

  /*
  The identifier for the currently selected user from the user input
   */
  const [userId, setUserId] = useState();

  /*
  The value of the currently selected role from the role input
   */
  const [role, setRole] = useState("PROJECT_USER");

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
  Watch for changes to the forms visibility, when it becomes visible
  set keyboard focus onto the user name input.
   */
  useEffect(() => {
    if (visible) {
      setTimeout(() => userRef.current.focus(), 100);
    }
  }, [visible]);

  /*
  Watch for changes to the debounced entered value for the user search.
  Once it changes send a request for filtered users.
   */
  useEffect(() => {
    if (debouncedQuery) {
      getAvailableUsersForProject(debouncedQuery).then((data) =>
        setResults(data)
      );
    } else {
      setResults([]);
    }
  }, [debouncedQuery]);

  /*
  Before rendering format the results into Select.Option
   */
  const options = results.map((u) => (
    <Option className="t-new-member" key={u.identifier}>
      <Text style={{ marginRight: SPACE_XS }}>{u.label}</Text>
      <Text type="secondary">{u.username}</Text>
    </Option>
  ));

  /**
   * Called when the user clicks the ok button.
   */
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
      <Button className="t-add-member-btn" onClick={() => setVisible(true)}>
        {i18n("AddMemberButton.label")}
      </Button>
      <Modal
        className="t-add-member-modal"
        visible={visible}
        okButtonProps={{ disabled: typeof userId === "undefined" }}
        onCancel={() => setVisible(false)}
        title={i18n("AddMemberButton.modal.title")}
        onOk={addUserToProject}
        okText={i18n("AddMemberButton.modal.okText")}
      >
        <Form layout="vertical" form={form} initialValues={{ role }}>
          <Form.Item
            label={i18n("AddMemberButton.modal.user-label")}
            help={i18n("AddMemberButton.modal.user-help")}
            name="user"
          >
            <Select
              ref={userRef}
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
          <Form.Item label={i18n("AddMemberButton.modal.role")} name="role">
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
