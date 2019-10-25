/*
 * Component to show a loading symbol with optional text
 * when data for a page is loading.
 */

import React from "react";
import { Spin } from "antd";
import { SPACE_SM } from "../../styles/spacing";

export function ContentLoading({ message = "Loading", ...props }) {
  return (
    <span>
      <Spin {...props} style={{ marginRight: SPACE_SM }} />
      {message}
    </span>
  );
}
