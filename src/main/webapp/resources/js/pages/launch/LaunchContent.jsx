import React from "react";
import { LaunchPageHeader } from "./LaunchPageHeader";
import { useLaunch } from "./launch-context";
import { LaunchForm } from "./LaunchForm";
import { Alert, Space } from "antd";
import { SPACE_LG } from "../../styles/spacing";
import { CART } from "../../utilities/events-utilities";

/**
 * React component to layout the content of the pipeline launch.
 * It will act as the top level logic controller.
 */
export function LaunchContent() {
  const [{ pipeline }] = useLaunch();

  const [validCart, setValidCart] = React.useState(true);

  /**
   * Listen for an empty cart, if empty, show warning that the pipeline cannot be run
   */
  React.useEffect(() => {
    function handleEmptyCart(e) {
      setValidCart(e.detail.count > 0);
    }

    window.addEventListener(CART.UPDATED, handleEmptyCart);
    return () => window.removeEventListener(CART.UPDATED, handleEmptyCart);
  }, []);

  return (
    <Space direction="vertical" style={{ width: `100%`, padding: SPACE_LG }}>
      <LaunchPageHeader pipeline={pipeline} />
      {validCart ? (
        <LaunchForm />
      ) : (
        <Alert
          type="error"
          showIcon
          message={i18n("LaunchContent.noSamples.message")}
          description={i18n("LaunchContent.noSamples.description")}
        />
      )}
    </Space>
  );
}
