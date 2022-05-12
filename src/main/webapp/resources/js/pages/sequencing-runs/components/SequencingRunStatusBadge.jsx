import React from "react";
import { Badge } from "antd";
import { grey7 } from "../../../styles/colors";
import styled from "styled-components";

const StyledBadge = styled(Badge)`
  .ant-badge-status-text {
    color: ${grey7};
  }
`;

/**
 * The badge for the sequence run status
 *
 * @param {string} status - sequence run status
 * @returns {*}
 * @constructor
 */
export function SequencingRunStatusBadge({ status }) {
  switch (status) {
    case "ERROR":
      return <StyledBadge status="error" text={status} />;
    case "UPLOADING":
      return <StyledBadge status="warning" text={status} />;
    case "COMPLETE":
      return <StyledBadge status="success" text={status} />;
    default:
      return <StyledBadge text={status} />;
  }
}
