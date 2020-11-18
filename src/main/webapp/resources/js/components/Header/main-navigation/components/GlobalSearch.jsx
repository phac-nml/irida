import React from "react";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { Form, Input } from "antd";
import { IconSearch } from "../../../icons/Icons";
import { grey3, grey6 } from "../../../../styles/colors";

export function GlobalSearch() {
  return (
    <Form
      layout="inline"
      style={{ display: "inline-block" }}
      method="post"
      action={setBaseUrl("/search")}
    >
      <Form.Item style={{ width: 300 }}>
        <Input
          prefix={<IconSearch style={{ color: grey6 }} />}
          placeholder={i18n("nav.main.search")}
          style={{
            border: "none",
            borderBottom: `1px solid ${grey3}`,
          }}
        />
      </Form.Item>
    </Form>
  );
}
