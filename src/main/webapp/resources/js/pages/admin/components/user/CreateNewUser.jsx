import React, { useEffect, useState } from "react";
import {
  Alert,
  Checkbox,
  Form,
  Input,
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
import { PagedTableContext } from "../../../../components/ant.design/PagedTable";
import { AddNewButton } from "../../../../components/Buttons/AddNewButton";
import { ScrollableModal } from "../../../../components/ant.design/ScrollableModal";
import {
  emailRuleList,
  firstNameRuleList,
  lastNameRuleList,
  localeRuleList,
  phoneNumberRuleList,
  roleRuleList,
  usernameRuleList,
  validatePassword,
} from "../../../../utilities/validation-utilities";
import { PasswordPolicyAlert } from "../../../../components/alerts/PasswordPolicyAlert";

/**
 * React component to display the create new user form.
 * @returns {*}
 * @constructor
 */
export default function CreateNewUser() {
  const { updateTable } = React.useContext(PagedTableContext);
  const { data: locales = [] } = useGetLocalesQuery();
  const { data: systemRoles = [] } = useGetSystemRolesQuery();
  const { data: emailConfigured = false } = useGetEmailConfiguredQuery();
  const [createUser] = useCreateNewUserMutation();
  const [form] = Form.useForm();
  const [activationEmail, setActivationEmail] = React.useState(emailConfigured);
  const [visible, setVisibility] = React.useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [mailFailure, setMailFailure] = useState(false);
  const usernameInput = React.useRef();
  const isAdmin = window.TL._USER.systemRole === "ROLE_ADMIN";

  useEffect(() => {
    if (visible) {
      usernameInput.current.focus();
    }
  }, [visible]);

  const onFormFinish = (values) => {
    setSubmitting(true);
    createUser({ ...values })
      .unwrap()
      .then(() => {
        notification.success({
          message: i18n("CreateNewUser.notification.success"),
          className: "t-user-page-notification-success",
        });
        form.resetFields();
        setVisibility(false);
        setMailFailure(false);
        updateTable();
      })
      .catch((error) => {
        notification.error({
          message: i18n("CreateNewUser.notification.error"),
        });
        if (error.status === 400) {
          const fields = Object.entries(error.data.errors).map(
            ([field, error]) => ({
              name: field,
              errors: [error],
            })
          );
          form.setFields(fields);
        } else if (error.status === 409) {
          form.setFieldsValue({ activate: false });
          setActivationEmail(false);
          setMailFailure(true);
        }
      })
      .finally(() => setSubmitting(false));
  };

  return (
    <>
      <AddNewButton
        className="t-add-user-btn"
        onClick={() => setVisibility(true)}
        text={i18n("CreateNewUser.button")}
      />
      <ScrollableModal
        title={i18n("CreateNewUser.title")}
        okText={i18n("CreateNewUser.form.button.submit")}
        okButtonProps={{
          className: "t-submit-btn",
          htmlType: "submit",
          loading: submitting,
          onClick: form.submit,
        }}
        onCancel={() => {
          setVisibility(false);
          form.resetFields();
        }}
        visible={visible}
        maxHeight={window.innerHeight - 250}
        width={640}
        maskClosable={false}
      >
        <Form
          form={form}
          layout="vertical"
          initialValues={{
            locale: "en",
            role: "ROLE_USER",
            activate: activationEmail,
          }}
          onFinish={onFormFinish}
        >
          <Form.Item
            label={i18n("CreateNewUser.form.username.label")}
            name="username"
            rules={usernameRuleList}
          >
            <Input ref={usernameInput} />
          </Form.Item>
          <Form.Item
            label={i18n("CreateNewUser.form.firstName.label")}
            name="firstName"
            rules={firstNameRuleList}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label={i18n("CreateNewUser.form.lastName.label")}
            name="lastName"
            rules={lastNameRuleList}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label={i18n("CreateNewUser.form.email.label")}
            name="email"
            rules={emailRuleList}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label={i18n("CreateNewUser.form.phoneNumber.label")}
            name="phoneNumber"
            rules={phoneNumberRuleList}
          >
            <Input />
          </Form.Item>
          <Form.Item
            label={i18n("CreateNewUser.form.locale.label")}
            name="locale"
            rules={localeRuleList}
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
          {isAdmin && (
            <Form.Item
              label={i18n("CreateNewUser.form.role.label")}
              name="role"
              rules={roleRuleList}
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
          )}
          {!emailConfigured && (
            <Alert
              style={{ marginBottom: SPACE_SM }}
              message={i18n("CreateNewUser.emailConfigured.alert.title")}
              description={
                <Typography.Paragraph>
                  {i18n("CreateNewUser.emailConfigured.alert.description")}
                </Typography.Paragraph>
              }
              type="warning"
              showIcon
            />
          )}
          {mailFailure && (
            <Alert
              style={{ marginBottom: SPACE_SM }}
              message={i18n("CreateNewUser.mailFailure.alert.title")}
              description={
                <Typography.Paragraph>
                  {i18n("CreateNewUser.mailFailure.alert.description")}
                </Typography.Paragraph>
              }
              type="error"
              showIcon
            />
          )}
          <Form.Item name="activate" valuePropName="checked">
            <Checkbox
              disabled={!emailConfigured || mailFailure}
              onChange={(e) => setActivationEmail(e.target.checked)}
            >
              {i18n("CreateNewUser.form.activate.label")}
            </Checkbox>
          </Form.Item>
          {!activationEmail && (
            <>
              <PasswordPolicyAlert />
              <Form.Item
                label={i18n("CreateNewUser.form.password.label")}
                name="password"
                rules={[
                  () => ({
                    validator(_, value) {
                      return validatePassword(value);
                    },
                  }),
                ]}
              >
                <Input.Password />
              </Form.Item>
            </>
          )}
        </Form>
      </ScrollableModal>
    </>
  );
}
