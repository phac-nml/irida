import React, { useEffect, useState } from "react";
import { Alert } from "antd";
import { LinkOutlined } from "@ant-design/icons";
import { removeGalaxySession } from "../../apis/galaxy/galaxy";
import { FONT_WEIGHT_HEAVY } from "../../styles/fonts";
import { SPACE_XS } from "../../styles/spacing";

const GalaxyMessage = () => {
  const galaxyUrl = window
    .decodeURI(window.GALAXY.URL)
    .split("/tool_runner")[0];

  return (
    <span>
      <span style={{ fontWeight: FONT_WEIGHT_HEAVY, marginRight: SPACE_XS }}>
        {window.GALAXY.TITLE}
      </span>
      {window.GALAXY.MESSAGE}{" "}
      <a target="_blank" rel="noopener noreferrer" href={galaxyUrl}>
        {galaxyUrl}
      </a>
      .
      <br />
      <a
        target="_blank"
        rel="noopener noreferrer"
        href="https://irida.corefacility.ca/documentation/user/user/samples/#galaxy-export"
      >
        {window.GALAXY.DOCUMENTATION} <LinkOutlined />
      </a>
    </span>
  );
};

export default function GalaxyAlert() {
  const [galaxy, setGalaxy] = useState(false);

  useEffect(() => setGalaxy(typeof window.GALAXY !== "undefined"), []);

  return galaxy ? (
    <Alert
      type="info"
      message={<GalaxyMessage />}
      banner
      closable
      closeText={window.GALAXY.CANCEL}
      onClose={removeGalaxySession}
    />
  ) : null;
}
