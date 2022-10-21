import { Button, Menu, Row, Space } from "antd";
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

  return (
    <ToolsWrapper>
      <MenuWrapper>
        <Menu
          mode="horizontal"
          selectedKeys={[location.pathname]}
          style={{ borderBottom: BORDERED_LIGHT }}
        >
          {fromGalaxy && (
            <Menu.Item key="/cart/galaxy">
              <Link to={setBaseUrl(`cart/galaxy`)}>
                {i18n("CartTools.menu.galaxy")}
              </Link>
            </Menu.Item>
          )}
          <Menu.Item key="/cart/pipelines">
            <Link to={setBaseUrl(`cart/pipelines`)}>
              {i18n("CartTools.menu.pipelines")}
            </Link>
          </Menu.Item>
        </Menu>
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
