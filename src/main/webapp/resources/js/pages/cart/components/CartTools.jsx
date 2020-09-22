import React, { Component, lazy, Suspense } from "react";

import { Link, Location, navigate, Router } from "@reach/router";
import { Menu, Row } from "antd";
import styled from "styled-components";
import { grey1, grey4 } from "../../../styles/colors";
import { SPACE_MD } from "../../../styles/spacing";
import { Pipelines } from "../../../components/pipelines/Pipelines";
import { BORDERED_LIGHT } from "../../../styles/borders";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { PipelineLaunchPage } from "../../pipeline-launch/PipelineLaunchPage";
import { LaunchComplete } from "../../../components/pipeline-launch-old/LaunchComplete";

const MenuWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 65px;
  border-bottom: ${BORDERED_LIGHT};
  background-color: ${grey1};
  width: 100%;

  .ant-menu {
    line-height: 65px;
  }
`;

/*
Lazy loaded since we do not need it unless we came from galaxy.
 */
const GalaxyApp = lazy(() => import("../../../components/galaxy/GalaxyApp"));

const GalaxyComponent = () => (
  <Suspense fallback={<div>Loading ...</div>}>
    <GalaxyApp path="cart/galaxy" default />
  </Suspense>
);

const ToolsWrapper = styled(Row)`
  height: 100%;
  width: 100%;
  border-right: ${BORDERED_LIGHT};
  background-color: ${grey1};
  position: relative;
`;

const ToolsInner = styled.div`
  padding: ${SPACE_MD};
  position: absolute;
  top: 67px;
  right: 0;
  bottom: 0;
  left: 0;
  overflow-x: auto;
  background-color: ${grey4};
`;

/**
 * Wrapper component for functionality available in the cart.
 */
export default class CartTools extends Component {
  constructor(props) {
    super(props);

    this.state = {
      fromGalaxy: typeof window.GALAXY !== "undefined",
    };
  }

  componentDidMount() {
    if (this.state.fromGalaxy) {
      /*
      If this is within a galaxy session, the user has the opportunity to remove the session
      from IRIDA.  When this happens this listener will ensure that the galaxy tab is removed
      from the UI, and the user is redirected to the pipelines page.
       */
      document.body.addEventListener("galaxy:removal", this.removeGalaxy);
    }
  }

  componentWillUnmount() {
    if (this.state.fromGalaxy) {
      /*
      Remove the galaxy listener to prevent leakage.
       */
      this.removeGalaxyListener();
    }
  }

  removeGalaxy = () => {
    // Remove the galaxy tab and redirect to the pipelines page.
    if (this.state.fromGalaxy) {
      this.setState(
        (prevState) => ({
          fromGalaxy: false,
        }),
        () => {
          navigate(setBaseUrl(`cart/pipelines`));
          this.removeGalaxyListener();
        }
      );
    }
  };

  removeGalaxyListener = () =>
    document.body.removeEventListener("galaxy:removal", this.removeGalaxy);

  render() {
    return (
      <ToolsWrapper>
        <Location>
          {() => (
            <div style={{ width: `100%` }}>
              <MenuWrapper>
                <Menu>
                  <Menu.Item>
                    <Link to={setBaseUrl(`/cart/pipelines`)}>
                      {i18n("CartTools.menu.pipelines")}
                    </Link>
                  </Menu.Item>
                </Menu>
              </MenuWrapper>
              <ToolsInner>
                <Router>
                  <Pipelines
                    path={setBaseUrl(`/cart/pipelines`)}
                    displaySelect={
                      this.props.count > 0 ||
                      window.PAGE.automatedProject != null
                    }
                    automatedProject={window.PAGE.automatedProject}
                  />
                  <PipelineLaunchPage
                    path={setBaseUrl(`/cart/pipelines/:pipelineId`)}
                  />
                  <LaunchComplete
                    path={setBaseUrl(`/cart/pipelines/complete`)}
                  />
                </Router>
              </ToolsInner>
            </div>
          )}
        </Location>
      </ToolsWrapper>
    );
  }
}
