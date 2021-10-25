import { Result } from "antd";
import React from "react";

/**
 * React component to show the successful completion of sharing/moving samples
 * @param message
 * @param removed
 * @returns {JSX.Element}
 * @constructor
 */
export function ShareSuccess({ message, removed }) {
  return (
    <Result
      status="success"
      title={
        removed
          ? i18n("ShareSamples.move.success")
          : i18n("ShareSamples.share.success")
      }
      subTitle={message}
    />
  );
}
