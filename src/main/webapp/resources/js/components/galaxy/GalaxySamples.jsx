import React from "react";
import { useStateValue } from "./GalaxyState";
import { Alert, Icon } from "antd";
import { getI18N } from "../../utilities/i18n-utilities";
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
          message={getI18N("GalaxySamples.processing")}
          icon={<Icon type="loading" />}
          showIcon
          type="info"
        />
      ) : null}
    </div>
  );
}
