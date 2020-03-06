import React from "react";

import { useStateValue } from "./GalaxyState";
import { Alert } from "antd";
import { SPACE_SM } from "../../styles/spacing";
import { IconLoading } from "../icons/Icons";

/**
 * Component to display the status of fetching samples in the proper Galaxy format.
 * @param {boolean} fetching
 */
export function GalaxySamples() {
  const [{ fetchingSamples }, dispatch] = useStateValue();

  return (
    <div style={{ marginBottom: SPACE_SM }}>
      {fetchingSamples ? (
        <Alert
          message={i18n("GalaxySamples.processing")}
          icon={<IconLoading />}
          showIcon
          type="info"
        />
      ) : null}
    </div>
  );
}
