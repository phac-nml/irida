import React, { useEffect, useState } from "react";
import { Alert, Button } from "antd";
import { Spinner } from "../../icons";
import { checkConnectionStatus } from "../../apis/remote-api/remote-api";
import { setBaseUrl } from "../../utilities/url-utilities";
import { LoginOutlined } from "@ant-design/icons";

export function RemoteApiStatus({ api, updateTable }) {
  const [loading, setLoading] = useState(true);
  const [validToken, setValidToken] = useState(false);

  useEffect(checkApiStatus, []);

  useEffect(() => {
    // Listen for a remote api connection
    window.addEventListener("message", updateRemoteApi, false);
    return () => window.removeEventListener("message", updateRemoteApi);
  }, []);

  function checkApiStatus() {
    setLoading(true);
    checkConnectionStatus({ id: api.id }).then(validity => {
      setLoading(false);
      setValidToken(validity);
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
    <Spinner text={i18n("RemoteApi.checking")} />
  ) : (
    <div>
      {validToken ? (
        <Alert message={i18n("RemoteApi.connected")} type="success" showIcon />
      ) : (
        <Button onClick={updateConnectionStatus}>
          <LoginOutlined /> {i18n("RemoteApi.disconnected")}
        </Button>
      )}
    </div>
  );
}
