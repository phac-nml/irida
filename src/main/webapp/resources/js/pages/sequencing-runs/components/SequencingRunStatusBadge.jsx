import React from "react";
import { Badge } from "antd";

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
      return <Badge status="error" text={status} />;
    case "UPLOADING":
      return <Badge status="warning" text={status} />;
    case "COMPLETE":
      return <Badge status="success" text={status} />;
    default:
      return <Badge text={status} />;
  }
}
