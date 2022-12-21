import React from "react";
import { SearchOutlined } from "@ant-design/icons";

/**
 * React component for the Ant Design table to display a filter icon.
 * Icon is blue when the filter is active
 * @param filtered - whether the filter is active
 */
export default ({ filtered }: { filtered: boolean }): JSX.Element => (
  <SearchOutlined style={{ color: filtered ? "#1890ff" : undefined }} />
);
