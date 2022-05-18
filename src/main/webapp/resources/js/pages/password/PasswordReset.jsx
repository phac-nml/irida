import React from "react";
import { render } from "react-dom";
import { Alert, Button, Col, Form, Input, List, Row, Typography } from "antd";
import { setBaseUrl } from "../../utilities/url-utilities";
import { IconLocked, IconUser } from "../../components/icons/Icons";
import { blue6 } from "../../styles/colors";
import { SPACE_MD, SPACE_SM } from "../../styles/spacing";
import { Provider } from "react-redux";
import store from "../store";

const { Item } = Form;

const { Title } = Typography;

/**
 * React component to layout the Password Reset page.
 * @returns {*}
 * @constructor
 */
export default function PasswordReset() {
  const [form] = Form.useForm();
  const passwordRules = [
    i18n("UserChangePasswordForm.alert.rule2"),
    i18n("UserChangePasswordForm.alert.rule3"),
    i18n("UserChangePasswordForm.alert.rule4"),
    i18n("UserChangePasswordForm.alert.rule5"),
    i18n("UserChangePasswordForm.alert.rule6"),
    "Passwords for admins are recommended to be atleast 11 characters",
    "Passwords should not form any words or contain any personal information",
  ];
  let tokens = window.location.href.split("/");
  let identifier = tokens[tokens.length - 1];

  return (
    <Row justify="center">
      <Col style={{ width: 600 }}>
        <Row justify="center" style={{ marginBottom: SPACE_MD }}>
          <img
            src={setBaseUrl("/resources/img/irida_logo_light.svg")}
            height={60}
            alt={i18n("generic.irida.website")}
          />
        </Row>
        <Alert
          style={{ marginBottom: SPACE_SM }}
          message={i18n("UserChangePasswordForm.alert.title")}
          description={
            <Typography.Paragraph>
              <List
                header={i18n("UserChangePasswordForm.alert.description")}
                dataSource={passwordRules}
                renderItem={(item) => <List.Item>- {item}</List.Item>}
              />
            </Typography.Paragraph>
          }
          type="info"
          showIcon
        />
        <Form form={form} name="resetPasswordForm" size="large">
          <Item
            name="password"
            rules={[
              {
                required: true,
                message: "Password is required",
              },
            ]}
          >
            <Input
              name="password"
              type="password"
              prefix={<IconLocked style={{ color: blue6 }} />}
              placeholder="New Password"
            />
          </Item>
          <Item>
            <Button id="t-submit-btn" type="primary" block htmlType="submit">
              Set Password
            </Button>
          </Item>
        </Form>
        <Button
          type="link"
          style={{ padding: 0 }}
          onClick={() => window.location.replace(setBaseUrl("/login"))}
        >
          Return to Login Page
        </Button>
      </Col>
    </Row>
  );
}

render(<PasswordReset />, document.querySelector("#root"));
