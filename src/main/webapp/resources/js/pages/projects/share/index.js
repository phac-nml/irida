import { Form, Menu, Space } from "antd";
import React from "react";
import { render } from "react-dom";
import { Provider } from "react-redux";
import {
  BrowserRouter as Router,
  Link,
  Route,
  Switch,
  useLocation,
} from "react-router-dom";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { ShareProject } from "./ShareProject";
import { ShareSamples } from "./ShareSamples";
import store from "./store";

/**
IGNORE THIS WILL BE MOVED / ENSURING ROUTER WORKING
 */
const ShareMetadata = () => <div>METAsDATA</div>;

/**
 * Base component for sharing samples between projects.
 *
 * @returns {JSX.Element}
 * @constructor
 */
function ShareLayout() {
  const location = useLocation();

  return (
    <Form layout="vertical">
      <Space direction="vertical" style={{ display: "block" }} size="large">
        <ShareProject />
        <Menu mode="horizontal" selectedKeys={[location.pathname]}>
          <Menu.Item key="/">
            <Link to="/">Samples</Link>
          </Menu.Item>
          <Menu.Item key="/metadata">
            <Link to="/metadata">Metadata</Link>
          </Menu.Item>
        </Menu>
        <Switch>
          <Route exact path="/">
            <ShareSamples />
          </Route>
          <Route path="/metadata">
            <ShareMetadata />
          </Route>
        </Switch>
      </Space>
    </Form>
  );
}

render(
  <Router basename={setBaseUrl("/projects/share")}>
    <Provider store={store}>
      <ShareLayout />
    </Provider>
  </Router>,
  document.querySelector("#root")
);
