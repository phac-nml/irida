import React from "react";
import { Badge } from "antd";

/**
 * React component to display the total number of items found in the global search
 * @param count
 * @constructor
 */
export default function SearchCount({
  count = 0,
}: {
  count: number | undefined;
}) {
  return (
    <Badge
      showZero
      overflowCount={1000}
      style={{ backgroundColor: `var(--grey-7)` }}
      count={count}
      className="t-count-badge"
    />
  );
}
