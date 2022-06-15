import React from "react";
import { SPACE_SM } from "../../styles/spacing";
import { Alert, List, Typography } from "antd";

/**
 * React component to render the password policy info within an alert.
 * @returns {*}
 * @constructor
 */
export function PasswordPolicyAlert() {
  const passwordRules = [
    i18n("validation-utilities.password.minimumLength"),
    i18n("validation-utilities.password.uppercase"),
    i18n("validation-utilities.password.lowercase"),
    i18n("validation-utilities.password.number"),
    i18n("validation-utilities.password.specialCharacters"),
  ];

  return (
    <Alert
      style={{ marginBottom: SPACE_SM }}
      message={i18n("PasswordPolicyAlert.title")}
      description={
        <Typography.Paragraph>
          <List
            header={i18n("PasswordPolicyAlert.description")}
            dataSource={passwordRules}
            renderItem={(item) => <List.Item>{item}</List.Item>}
          />
        </Typography.Paragraph>
      }
      type="info"
      showIcon
    />
  );
}
