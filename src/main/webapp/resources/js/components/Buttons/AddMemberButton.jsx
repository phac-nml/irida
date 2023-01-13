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
import { useMetadataRoles } from "../../contexts/metadata-roles-context";
import { useProjectRoles } from "../../contexts/project-roles-context";
import { useDebounce, useResetFormOnCloseModal } from "../../hooks";
import { SPACE_XS } from "../../styles/spacing";

const { Option } = Select;
const { Text } = Typography;

export function AddMemberButton({
  label,
  modalTitle,
  addMemberFn = () => undefined,
  getAvailableMembersFn = () => undefined,
  addMemberSuccessFn = () => undefined,
}) {
  /*
  Required a reference to the user select input so that focus can be set
  to it when the window opens.
   */
  const userRef = useRef();

  const { roles: projectRoles } = useProjectRoles();
  const { roles: metadataRoles } = useMetadataRoles();

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
  const [projectRole, setProjectRole] = useState("PROJECT_USER");
  const [metadataRole, setMetadataRole] = useState("LEVEL_1");

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
      getAvailableMembersFn(debouncedQuery).then((data) => setResults(data));
    } else {
      setResults([]);
    }
  }, [debouncedQuery, getAvailableMembersFn]);

  /*
  Watch for changes to the forms visibility, when it becomes visible
  set keyboard focus onto the user name input.
   */
  useEffect(() => {
    if (visible) {
      setTimeout(() => userRef.current.focus(), 100);
    }
  }, [visible]);

  const addMember = () => {
    addMemberFn({ id: userId, projectRole, metadataRole })
      .then((message) => {
        addMemberSuccessFn();
        notification.success({ message });
        form.resetFields();
        setVisible(false);
      })
      .catch((message) => notification.error({ message }));
  };

  /*
  Before rendering format the results into Select.Option
   */
  const options = results.map((u) => (
    <Option className="t-new-member" key={u.identifier}>
      <Text style={{ marginRight: SPACE_XS }}>{u.label}</Text>
      <Text type="secondary">{u.username}</Text>
    </Option>
  ));

  const onCancel = () => {
    form.resetFields();
    setVisible(false);
  };

  return (
    <>
      <Button className="t-add-member-btn" onClick={() => setVisible(true)}>
        {label}
      </Button>
      <Modal
        className="t-add-member-modal"
        visible={visible}
        okButtonProps={{ disabled: typeof userId === "undefined" }}
        onCancel={onCancel}
        title={modalTitle}
        onOk={addMember}
        okText={i18n("AddMemberButton.modal.okText")}
      >
        <Form
          layout="vertical"
          form={form}
          initialValues={{ projectRole, metadataRole }}
        >
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
          <Form.Item
            label={i18n("AddMemberButton.modal.projectRole")}
            name="projectRole"
          >
            <Radio.Group
              style={{ display: "flex" }}
              onChange={(e) => {
                setProjectRole(e.target.value);
                if (e.target.value === "PROJECT_OWNER") {
                  setMetadataRole("LEVEL_4");
                }
              }}
            >
              {projectRoles.map((role) => (
                <Radio.Button
                  key={role.value}
                  value={role.value}
                  className={`t-project-role-${role.label.toLowerCase()}`}
                >
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
              disabled={projectRole === "PROJECT_OWNER"}
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
