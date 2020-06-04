
import { Layout, Menu } from "antd";
import { grey1 } from "../../../styles/colors";
import { Link, Location, Router } from "@reach/router";
import { SETTINGS } from "../../analysis/routes";
import { SPACE_MD } from "../../../styles/spacing";
import React, { Suspense } from "react";
import { ContentLoading } from "../../../components/loader";
const { SubMenu } = Menu;

export default function AnalysisSettings() {
  const pathRegx = new RegExp(/([a-zA-Z]+)$/);

  return (
    <Layout>
      <h1>ADMIN PANEL Test</h1>
      {/*<Menu width={200} theme="Dark">*/}
      {/*  <Location>*/}
      {/*    {props => {*/}
      {/*      const keyname = props.location.pathname.match(pathRegx);*/}
      {/*      return (*/}
      {/*        <Menu*/}
      {/*          mode="vertical"*/}
      {/*          selectedKeys={[keyname ? keyname[1] : SETTINGS.DETAILS]}*/}
      {/*        >*/}
      {/*          <Menu.Item key="details">*/}
      {/*            <Link to={`${DEFAULT_URL}/${SETTINGS.DETAILS}`}>*/}
      {/*              {i18n("AnalysisDetails.details")}*/}
      {/*            </Link>*/}
      {/*          </Menu.Item>*/}
      {/*          <SubMenu key="sub1" title="Navigation One">*/}
      {/*            <Menu.ItemGroup title="Item 1">*/}
      {/*              <Menu.Item key="1">Option 1</Menu.Item>*/}
      {/*              <Menu.Item key="2">Option 2</Menu.Item>*/}
      {/*            </Menu.ItemGroup>*/}
      {/*          </SubMenu>*/}
      {/*        </Menu>*/}
      {/*      );*/}
      {/*    }}*/}
      {/*  </Location>*/}
      {/*</Menu>*/}

      {/*<Layout style={{ paddingLeft: SPACE_MD, backgroundColor: grey1 }}>*/}
      {/*  <Content>*/}
      {/*  </Content>*/}
      {/*</Layout>*/}
    </Layout>
  );
}