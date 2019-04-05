import React, { Component } from "react";
import { Location, navigate, Router } from "@reach/router";
import { Row } from "antd";
import styled from "styled-components";
import { CartToolsMenu } from "./CartToolsMenu";
import {
  grey1,
  grey2,
  grey3,
  grey4,
  COLOR_BORDER_LIGHT
} from "../../../styles/colors";
import { SPACE_MD } from "../../../styles/spacing";
import { getI18N } from "../../../utilities/i18n-utilties";
import { GalaxyExport } from "../../../components/galaxy/GalaxyExport";
import { Pipelines } from "../../../components/pipelines/Pipelines";

const ToolsWrapper = styled(Row)`
  height: 100%;
  width: 100%;
  border-right: 1px solid ${COLOR_BORDER_LIGHT};
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
`;

/**
 * Wrapper component for functionality available in the cart.
 */
export default class CartTools extends Component {
  constructor(props) {
    super(props);

    const fromGalaxy = typeof window.GALAXY !== "undefined";
    this.state = {
      fromGalaxy,
      paths: [
        fromGalaxy
          ? {
              key: "/cart/galaxy",
              link: "cart/galaxy",
              text: getI18N("CartTools.menu.galaxy"),
              component: (
                <GalaxyExport key="cart/galaxy" path="cart/galaxy" default />
              )
            }
          : null,
        {
          key: "/cart/pipelines",
          link: "cart/pipelines",
          text: getI18N("CartTools.menu.pipelines"),
          component: (
            <Pipelines
              key="/cart/pipelines"
              path="cart/pipelines"
              displaySelect={props.count > 0}
              default={!fromGalaxy}
            />
          )
        }
      ].filter(Boolean)
    };
  }

  componentDidMount() {
    if (this.state.fromGalaxy) {
      /*
      If this is within a galaxy session, the user has the opportunity to remove the session
      from IRIDA.  When this happens this listener will ensure that the galaxy tab is removed
      from the UI, and the user is redirected to the pipelines page.
       */
      this.galaxySesssionListener = document.body.addEventListener(
        "galaxy:removal",
        this.removeGalaxy
      );
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
        prevState => ({
          fromGalaxy: false,
          paths: prevState.paths.splice(1)
        }),
        () => {
          navigate("/cart/pipelines");
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
          {({ location }) => (
            <>
              <CartToolsMenu
                pathname={location.pathname}
                paths={this.state.paths}
                toggleSidebar={this.props.toggleSidebar}
                collapsed={this.props.collapsed}
              />
              <ToolsInner>
                <Router>{this.state.paths.map(path => path.component)}</Router>
              </ToolsInner>
            </>
          )}
        </Location>
      </ToolsWrapper>
    );
  }
}
