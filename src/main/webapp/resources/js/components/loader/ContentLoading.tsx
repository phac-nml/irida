/*
 * Component to show a loading symbol with optional text
 * when data for a page is loading.
 */

import React, { ComponentProps, Props } from "react";
import { Spin, SpinProps } from "antd";
import { SPACE_SM } from "../../styles/spacing";

/**
 * Stateless UI component for displaying a loading symbol with optional text
 */

export function ContentLoading({
  message = "Loading",
  ...props
}: {
  message: string;
  props: SpinProps;
}): JSX.Element {
  return (
    <span>
      <Spin {...props} style={{ marginRight: SPACE_SM }} />
      {message}
    </span>
  );
}
