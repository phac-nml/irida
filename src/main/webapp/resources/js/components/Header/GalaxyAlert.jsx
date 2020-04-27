import React from "react";
import { Alert } from "antd";
import { removeGalaxySession } from "../../apis/galaxy/galaxy";
import { FONT_WEIGHT_HEAVY } from "../../styles/fonts";
import { SPACE_XS } from "../../styles/spacing";
import { IconLinkOut } from "../icons/Icons";

export default function GalaxyAlert() {
  const galaxyUrl = window
    .decodeURI(window.GALAXY.URL)
    .split("/tool_runner")[0];

  const message = (
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
        {window.GALAXY.DOCUMENTATION} <IconLinkOut />
      </a>
    </span>
  );

  return (
    <Alert
      type="info"
      message={message}
      banner
      closable
      closeText={window.GALAXY.CANCEL}
      onClose={removeGalaxySession}
    />
  );
}
