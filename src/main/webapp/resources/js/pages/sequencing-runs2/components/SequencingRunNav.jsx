import React from "react";
import { Link, useLocation } from "react-router-dom";
import { Menu } from "antd";

/**
 * React component to display the sequencing run navigation.
 * @returns {*}
 * @constructor
 */
export default function SequencingRunNav() {
  const location = useLocation();
  const [selectedKeys, setSelectedKey] = React.useState("details");

  React.useEffect(() => {
    const lastElement = location.pathname.split("/").pop();
    if (lastElement.match("details|files")) {
      setSelectedKey(lastElement);
    }
  }, [location]);

  return (
    <Menu mode="inline" selectedKeys={[selectedKeys]}>
      <Menu.Item key="details">
        <Link to="details">Details</Link>
      </Menu.Item>
      <Menu.Item key="files">
        <Link to="files">Files</Link>
      </Menu.Item>
    </Menu>
  );
}