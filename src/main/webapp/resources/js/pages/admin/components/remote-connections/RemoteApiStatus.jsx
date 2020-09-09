import React, { useEffect, useState } from "react";
import { Alert, Button } from "antd";
import { checkConnectionStatus } from "../../../../apis/remote-api/remote-api";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import { IconLoading, IconLogin } from "../../../../components/icons/Icons";
import { SPACE_XS } from "../../../../styles/spacing";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";

/**
 * React component to render the status of a Remote API.
 * If the API is not connected it will present the user a button allowing
 * them to connect.
 * @param {object} api - details about the remote API
 * @returns {*}
 * @constructor
 */
export function RemoteApiStatus({ api }) {
  const [loading, setLoading] = useState(true);
  const [expiration, setExpiration] = useState(undefined);

  useEffect(checkApiStatus, []);

  useEffect(() => {
    // Listen for a remote api connection
    window.addEventListener("message", updateRemoteApi, false);
    return () => window.removeEventListener("message", updateRemoteApi);
  }, []);

  function checkApiStatus() {
    setLoading(true);
    checkConnectionStatus({ id: api.id }).then((data) => {
      setLoading(false);
      setExpiration(data);
    });
  }

  function updateRemoteApi(event) {
    if (
      event.origin === window.location.origin &&
      event.data === "remote_api_connect"
    ) {
      checkApiStatus();
    }
  }

  function updateConnectionStatus() {
    window.open(
      setBaseUrl(`remote_api/connect/${api.id}`),
      "",
      "height=400, width=600, chrome=yes, centerscreen"
    );
  }

  return loading ? (
    <span>
      <IconLoading style={{ marginRight: SPACE_XS }} />
      {i18n("RemoteApi.checking")}
    </span>
  ) : (
    <div>
      {expiration ? (
        <Alert
          message={i18n(
            "RemoteApi.connected",
            formatInternationalizedDateTime(expiration)
          )}
          type="success"
          showIcon
        />
      ) : (
        <Button onClick={updateConnectionStatus} icon={<IconLogin />}>
          {i18n("RemoteApi.disconnected")}
        </Button>
      )}
    </div>
  );
}
