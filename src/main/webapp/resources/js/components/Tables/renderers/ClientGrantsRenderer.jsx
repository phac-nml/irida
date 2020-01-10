import React from "react";
import { Tag } from "antd";
import { SPACE_XS } from "../../../styles/spacing";

export function ClientGrantsRenderer({ data }) {
  // Default colors for displaying grant types
  const colors = { password: "geekblue", authorization_code: "volcano" };
  // Comes back as a coma separated text list
  return data.grants.split(",").map(grant => (
    <Tag
      key={grant}
      color={colors[grant] || ""}
      style={{ marginRight: SPACE_XS }}
    >
      {grant}
    </Tag>
  ));
}
