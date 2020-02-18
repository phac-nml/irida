import React from "react";
import { Breadcrumb } from "antd";
import { grey2, grey4, grey8 } from "../../styles/colors";
import { SPACE_MD, SPACE_XS } from "../../styles/spacing";
import { setBaseUrl } from "../../utilities/url-utilities";
import { HomeTwoTone } from "@ant-design/icons";

export function Breadcrumbs({ crumbs = [] }) {
  return crumbs.length ? (
    <Breadcrumb
      style={{
        backgroundColor: grey2,
        padding: `${SPACE_XS} 15px`,
        justifyContent: "flex-start",
        borderBottom: `1px solid ${grey4}`,
        marginBottom: SPACE_MD
      }}
    >
      <Breadcrumb.Item>
        <a href={setBaseUrl("")}>
          <HomeTwoTone style={{ color: grey8 }} />
        </a>
      </Breadcrumb.Item>
      {crumbs.map(crumb => (
        <Breadcrumb.Item key={crumb.label}>
          <a href={crumb.url}>{crumb.label}</a>
        </Breadcrumb.Item>
      ))}
    </Breadcrumb>
  ) : null;
}
