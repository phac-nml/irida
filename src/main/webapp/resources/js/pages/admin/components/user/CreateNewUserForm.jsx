import React from "react";
import {
  Alert,
  Button,
  Checkbox,
  Form,
  Input,
  List,
  notification,
  Select,
  Typography,
} from "antd";
import { useCreateNewUserMutation } from "../../../../apis/users/users";
import {
  useGetEmailConfiguredQuery,
  useGetLocalesQuery,
  useGetSystemRolesQuery,
} from "../../../../apis/settings/settings";
import { SPACE_SM } from "../../../../styles/spacing";
import { useVisibility } from "../../../../contexts/visibility-context";
import { PagedTableContext } from "../../../../components/ant.design/PagedTable";

/**
 * React component to display the create new user form.
 * @returns {*}
 * @constructor
 */
export default function CreateNewUserForm() {
  const { updateTable } = React.useContext(PagedTableContext);
  const { data: locales = [] } = useGetLocalesQuery();
  const { data: systemRoles = [] } = useGetSystemRolesQuery();
  const { data: emailConfigured = false } = useGetEmailConfiguredQuery();
  const [createUser] = useCreateNewUserMutation();
  const [form] = Form.useForm();
  const [, setVisibility] = useVisibility();

  const passwordRules = [
    i18n("CreateNewUserForm.changePassword.alert.rule2"),
    i18n("CreateNewUserForm.changePassword.alert.rule3"),
    i18n("CreateNewUserForm.changePassword.alert.rule4"),
    i18n("CreateNewUserForm.changePassword.alert.rule5"),
    i18n("CreateNewUserForm.changePassword.alert.rule6"),
  ];

  const onFormFinish = (values) => {
    createUser({ ...values })
      .unwrap()
      .then(() => {
        console.log("SUCCESS");
        notification.success({
          message: i18n("CreateNewUserForm.notification.success"),
          className: "t-user-page-notification-success",
        });
        form.resetFields();
        setVisibility(false);
        updateTable();
      })
      .catch((error) => {
        console.log("FAILED");
        notification.error({
          message: i18n("CreateNewUserForm.notification.error"),
        });
        const fields = Object.entries(error.data).map(([field, error]) => ({
          name: field,
          errors: [error],
        }));
        form.setFields(fields);
      });
  };

  return (
    <Form form={form} layout="vertical" onFinish={onFormFinish}>
      <Form.Item
        label={i18n("CreateNewUserForm.form.username.label")}
        name="username"
        rules={[
          {
            required: true,
            message: i18n("CreateNewUserForm.form.username.required"),
          },
        ]}
      >
        <Input />
      </Form.Item>
      <Form.Item
        label={i18n("CreateNewUserForm.form.firstName.label")}
        name="firstName"
        rules={[
          {
            required: true,
            message: i18n("CreateNewUserForm.form.firstName.required"),
          },
        ]}
      >
        <Input />
      </Form.Item>
      <Form.Item
        label={i18n("CreateNewUserForm.form.lastName.label")}
        name="lastName"
        rules={[
          {
            required: true,
            message: i18n("CreateNewUserForm.form.lastName.required"),
          },
        ]}
      >
        <Input />
      </Form.Item>
      <Form.Item
        label={i18n("CreateNewUserForm.form.email.label")}
        name="email"
        rules={[
          {
            required: true,
            message: i18n("CreateNewUserForm.form.email.required"),
          },
          {
            type: "email",
            message: i18n("CreateNewUserForm.form.email.type"),
          },
        ]}
      >
        <Input />
      </Form.Item>
      <Form.Item
        label={i18n("CreateNewUserForm.form.phoneNumber.label")}
        name="phoneNumber"
        rules={[
          {
            required: true,
            message: i18n("CreateNewUserForm.form.phoneNumber.required"),
          },
        ]}
      >
        <Input />
      </Form.Item>
      <Form.Item
        label={i18n("CreateNewUserForm.form.locale.label")}
        name="locale"
        rules={[
          {
            required: true,
            message: i18n("CreateNewUserForm.form.locale.required"),
          },
        ]}
      >
        <Select>
          {locales.map((locale, index) => (
            <Select.Option
              key={`create-new-user-account-locale-${index}`}
              value={locale.language}
            >
              {locale.name}
            </Select.Option>
          ))}
        </Select>
      </Form.Item>
      <Form.Item
        label={i18n("CreateNewUserForm.form.role.label")}
        name="role"
        rules={[
          {
            required: true,
            message: i18n("CreateNewUserForm.form.role.required"),
          },
        ]}
      >
        <Select>
          {systemRoles.map((role, index) => (
            <Select.Option
              key={`create-new-user-account-role-${index}`}
              value={role.code}
            >
              {role.name}
            </Select.Option>
          ))}
        </Select>
      </Form.Item>
      {!emailConfigured && (
        <Alert
          style={{ marginBottom: SPACE_SM }}
          message={i18n("CreateNewUserForm.emailConfigured.alert.title")}
          description={
            <Typography.Paragraph>
              {i18n("CreateNewUserForm.emailConfigured.alert.description")}
            </Typography.Paragraph>
          }
          type="warning"
          showIcon
        />
      )}
      <Form.Item name="activate" valuePropName="checked">
        <Checkbox disabled={!emailConfigured}>
          {i18n("CreateNewUserForm.form.activate.label")}
        </Checkbox>
      </Form.Item>
      <Alert
        style={{ marginBottom: SPACE_SM }}
        message={i18n("CreateNewUserForm.changePassword.alert.title")}
        description={
          <Typography.Paragraph>
            <List
              header={i18n(
                "CreateNewUserForm.changePassword.alert.description"
              )}
              dataSource={passwordRules}
              renderItem={(item) => <List.Item>{item}</List.Item>}
            />
          </Typography.Paragraph>
        }
        type="info"
        showIcon
      />
      <Form.Item
        label={i18n("CreateNewUserForm.form.label.password")}
        name="password"
        rules={[
          {
            required: true,
            message: i18n("CreateNewUserForm.changePassword.alert.rule1"),
          },
          {
            min: 8,
            message: i18n("CreateNewUserForm.changePassword.alert.rule2"),
          },
          {
            pattern: new RegExp("^.*[A-Z].*$"),
            message: i18n("CreateNewUserForm.changePassword.alert.rule3"),
          },
          {
            pattern: new RegExp("^.*[a-z].*$"),
            message: i18n("CreateNewUserForm.changePassword.alert.rule4"),
          },
          {
            pattern: new RegExp("^.*[0-9].*$"),
            message: i18n("CreateNewUserForm.changePassword.alert.rule5"),
          },
          {
            pattern: new RegExp("^.*[^A-Za-z0-9].*$"),
            message: i18n("CreateNewUserForm.changePassword.alert.rule6"),
          },
        ]}
      >
        <Input.Password />
      </Form.Item>
      <Form.Item
        label={i18n("CreateNewUserForm.form.label.confirmPassword")}
        name="confirmPassword"
        dependencies={["password"]}
        rules={[
          {
            required: true,
            message: i18n("CreateNewUserForm.changePassword.alert.rule1"),
          },
          ({ getFieldValue }) => ({
            validator(_, value) {
              if (!value || getFieldValue("password") === value) {
                return Promise.resolve();
              }
              return Promise.reject(
                new Error(i18n("CreateNewUserForm.changePassword.alert.rule7"))
              );
            },
          }),
        ]}
      >
        <Input.Password />
      </Form.Item>
      <Form.Item>
        <Button className="t-submit-btn" type="primary" htmlType="submit">
          {i18n("CreateNewUserForm.form.button.submit")}
        </Button>
      </Form.Item>
    </Form>
  );
}
