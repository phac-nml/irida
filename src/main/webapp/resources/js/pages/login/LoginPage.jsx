import React from "react";
import { render } from "react-dom";
import { Alert, Card } from "antd";
import { setBaseUrl } from "../../utilities/url-utilities";
import { LoginFrom } from "../../components/login/LoginForm";

function LoginPage() {
  return (
    <Card style={{ width: 400, margin: 24 }}>
      <img
        src={setBaseUrl("/resources/img/irida_logo_light.svg")}
        alt=""
        style={{ marginBottom: 24 }}
      />
      {window.PAGE?.hasErrors ? (
        <Alert
          type="error"
          style={{ marginBottom: 24 }}
          message={
            <span className="t-login-error">
              {i18n("LoginPage.error.message")}
            </span>
          }
          description={
            <>
              {i18n("LoginPage.error.description")}{" "}
              <a href={setBaseUrl("password_reset")}>
                {i18n("LoginPage.recover")}
              </a>
            </>
          }
          showIcon
          closable
        />
      ) : null}
      <LoginFrom />
    </Card>
  );
}

render(<LoginPage />, document.querySelector("#login-root"));
