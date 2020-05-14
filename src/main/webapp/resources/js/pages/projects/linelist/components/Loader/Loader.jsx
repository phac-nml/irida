import React from "react";

import { Alert } from "antd";
import { SPACE_XS } from "../../../../../styles/spacing";
import { IconLoading } from "../../../../../components/icons/Icons";

/**
 * React component to display a message to the user that the page is loading.
 * @returns {*}
 * @constructor
 */
export const Loader = () => (
  <Alert
    message={i18n("linelist.Loader.message")}
    description={
      <div>
        <IconLoading style={{ marginRight: SPACE_XS }} />
        {i18n("linelist.Loader.description")}
      </div>
    }
    type="info"
  />
);
