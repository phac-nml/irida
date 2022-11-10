import React from "react";
import { Badge } from "antd";

export default function SearchCount({ count }: { count: number }) {
  return (
    <Badge
      showZero
      overflowCount={1000}
      style={{ backgroundColor: `var(--grey-7)` }}
      count={count}
    />
  );
}
