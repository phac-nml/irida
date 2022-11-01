import { Button, Menu, Row, Space } from "antd";
import type { MenuProps } from "antd";
import React, { lazy, Suspense } from "react";
import {
  BrowserRouter,
  Link,
  Route,
  Routes,
  useLocation,
  useNavigate,
} from "react-router-dom";
import styled from "styled-components";
import { AnalysesQueue } from "../../../components/AnalysesQueue";
import { IconMenuFold, IconMenuUnfold } from "../../../components/icons/Icons";
import { Pipelines } from "../../../components/pipelines/Pipelines";
import { BORDERED_LIGHT } from "../../../styles/borders";
import { grey1, grey6 } from "../../../styles/colors";
import { SPACE_MD } from "../../../styles/spacing";
import { setBaseUrl } from "../../../utilities/url-utilities";
import HorizontalMenu from "../../../components/ant.design/HorizontalMenu";

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
  background-color: var(--grey-1);
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

const MenuWrapper = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 65px;
  border-bottom: ${BORDERED_LIGHT};
  width: 100%;
  .ant-menu {
    line-height: 65px;
    background-color: transparent;
  }
`;

function CartToolsContent({ count, toggleSidebar, collapsed }) {
  const location = useLocation();
  const navigate = useNavigate();

  const [fromGalaxy, setFromGalaxy] = React.useState(
    () => typeof window.GALAXY !== "undefined"
  );

  React.useEffect(() => {
    function removeGalaxy() {
      setFromGalaxy(false);
      navigate(setBaseUrl(`cart/pipelines`));
    }

    if (fromGalaxy) {
      /*
      If this is within a galaxy session, the user has the opportunity to remove the session
      from IRIDA.  When this happens this listener will ensure that the galaxy tab is removed
      from the UI, and the user is redirected to the pipelines page.
       */
      document.body.addEventListener("galaxy:removal", removeGalaxy);
    }

    return () => {
      document.body.removeEventListener("galaxy:removal", removeGalaxy);
    };
  }, [fromGalaxy]);

  const BASE_URL = setBaseUrl("/cart");

  const menuItems: MenuProps["items"] = [
    ...(fromGalaxy
      ? [
          {
            key: "cart:galaxy",
            label: i18n("CartTools.menu.galaxy"),
          },
        ]
      : []),
    {
      key: "cart:pipelines",
      label: i18n("CartTools.menu.pipelines"),
    },
  ];

  const onClick: MenuProps["onClick"] = ({ key }) => {
    if (key === "cart:galaxy") {
      navigate(setBaseUrl(`cart/galaxy`));
    } else if (key === "cart:pipelines") {
      navigate(setBaseUrl(`cart/pipelines`));
    } else {
      throw new Error(`Cannot find path for key: ${key}`);
    }
  };

  return (
    <ToolsWrapper>
      <MenuWrapper>
        <HorizontalMenu selectedKeys={[location.pathname]} items={menuItems} />
        <Space align="center" style={{ padding: `0 ${SPACE_MD}` }}>
          <AnalysesQueue />
          <Button
            type="link"
            onClick={toggleSidebar}
            icon={
              collapsed ? (
                <IconMenuFold style={{ color: grey6, fontSize: 24 }} />
              ) : (
                <IconMenuUnfold style={{ color: grey6, fontSize: 24 }} />
              )
            }
          />
        </Space>
      </MenuWrapper>
      <ToolsInner>
        <Routes>
          {fromGalaxy && (
            <Route
              path={`${BASE_URL}/galaxy`}
              element={<GalaxyComponent key="galaxy" />}
            />
          )}
          <Route
            path={`${BASE_URL}/pipelines`}
            element={
              <Pipelines
                key="pipelines"
                displaySelect={!!count || window.PAGE.automatedProject != null}
              />
            }
          />
        </Routes>
      </ToolsInner>
    </ToolsWrapper>
  );
}

export default function CartTools({ ...props }) {
  return (
    <BrowserRouter>
      <CartToolsContent {...props} />
    </BrowserRouter>
  );
}
