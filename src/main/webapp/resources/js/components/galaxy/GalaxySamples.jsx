import React from "react";

import { useStateValue } from "./GalaxyState";
import { Alert } from "antd";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSpinner } from "@fortawesome/free-solid-svg-icons";
import { SPACE_SM } from "../../styles/spacing";

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
          icon={<FontAwesomeIcon icon={faSpinner} pulse />}
          showIcon
          type="info"
        />
      ) : null}
    </div>
  );
}
