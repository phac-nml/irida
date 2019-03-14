import React from "react";
import { Location, Router } from "@reach/router";
import { Row } from "antd";
import styled from "styled-components";
import { CartToolsMenu } from "./CartToolsMenu";
import { COLOR_BACKGROUND_LIGHTEST } from "../../../styles/colors";
import { SPACE_MD } from "../../../styles/spacing";
import { getI18N } from "../../../utilities/i18n-utilties";
import { GalaxyExport } from "../../../components/galaxy/GalaxyExport";
import { Pipelines } from "../../../components/pipelines/Pipelines";

const ToolsWrapper = styled(Row)`
  height: 100%;
  width: 100%;
  background-color: ${COLOR_BACKGROUND_LIGHTEST};
  position: relative;
`;

const ToolsInner = styled.div`
  padding: ${SPACE_MD};
  position: absolute;
  top: 50px;
  right: 0;
  bottom: 0;
  left: 0;
  overflow-x: auto;
`;

/**
 * Wrapper component for functionality available in the cart.
 */
export function CartTools() {
  const fromGalaxy = window.PAGE.galaxyCallback !== null;

  /*
   * Update here to add new tab items to the page.
   */
  const paths = [
    {
      key: "/cart/pipelines",
      link: "cart/pipelines",
      text: getI18N("CartTools.menu.pipelines"),
      component: (
        <Pipelines
          key="cart/pipelines"
          path="cart/pipelines"
          default={!fromGalaxy}
        />
      )
    }
  ];

  if (fromGalaxy) {
    paths.unshift({
      key: "/cart/galaxy",
      link: "cart/galaxy",
      text: getI18N("CartTools.menu.galaxy"),
      component: <GalaxyExport key="cart/galaxy" path="cart/galaxy" default />
    });
  }

  return (
    <ToolsWrapper>
      <Location>
        {({ location }) => (
          <>
            <CartToolsMenu
              pathname={location.pathname || "/cart/galaxy"}
              paths={paths}
            />
            <ToolsInner>
              <Router>{paths.map(path => path.component)}</Router>
            </ToolsInner>
          </>
        )}
      </Location>
    </ToolsWrapper>
  );
}
