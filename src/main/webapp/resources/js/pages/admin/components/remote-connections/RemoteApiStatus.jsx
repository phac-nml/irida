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
 * @param {function} onConnect - what do when the connection is made.
 * @returns {*}
 * @constructor
 */
export function RemoteApiStatus({ api, onConnect = () => {} }) {
  const [loading, setLoading] = useState(true);
  const [expiration, setExpiration] = useState(undefined);

  useEffect(checkApiStatus, []);

  useEffect(() => {
    // Listen for a remote api connection
    window.addEventListener("message", updateRemoteApi, false);
    return () => window.removeEventListener("message", updateRemoteApi);
  }, []);

  useEffect(() => checkApiStatus(), [api.id]);

  function checkApiStatus() {
    setLoading(true);
    checkConnectionStatus({ id: api.id }).then((data) => {
      setLoading(false);
      setExpiration(data);
      data && onConnect();
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
    // Fixes dual-screen position
    const dualScreenLeft =
      window.screenLeft !== undefined ? window.screenLeft : window.screenX;
    const dualScreenTop =
      window.screenTop !== undefined ? window.screenTop : window.screenY;

    const width = window.innerWidth
      ? window.innerWidth
      : document.documentElement.clientWidth
      ? document.documentElement.clientWidth
      : screen.width;
    const height = window.innerHeight
      ? window.innerHeight
      : document.documentElement.clientHeight
      ? document.documentElement.clientHeight
      : screen.height;
    const w = 600;
    const h = 400;

    const systemZoom = width / window.screen.availWidth;
    const left = (width - w) / 2 / systemZoom + dualScreenLeft;
    const top = (height - h) / 2 / systemZoom + dualScreenTop;

    window.open(
      setBaseUrl(`remote_api/connect/${api.id}`),
      "",
      `height=${h}, width=${w}, chrome=yes, top=${top}, left=${left}`
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
          className="t-remote-status-connected"
          message={i18n(
            "RemoteApi.connected",
            formatInternationalizedDateTime(expiration)
          )}
          type="success"
          showIcon
        />
      ) : (
        <Button
          className="t-remote-status-connect"
          onClick={updateConnectionStatus}
          icon={<IconLogin />}
        >
          {i18n("RemoteApi.disconnected")}
        </Button>
      )}
    </div>
  );
}
