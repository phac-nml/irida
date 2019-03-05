import React from "react";
import { Alert } from "antd";

const message = (
  <span>
    <strong>Galaxy Export Session</strong> Galaxy Export Session: IRIDA is set to upload your selected data to Galaxy. <a href=""> Read the Official Documentation</a>
  </span>
);

export default function GalaxyAlert({ removeGalaxy }) {
  return (
    <Alert
      type="info"
      message={message}
      banner
      closable
      closeText={"__ Cancel Galaxy Export __"}
      onClose={removeGalaxy}
    />
  );
}
