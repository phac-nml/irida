import React from "react";
import { Col, Layout, PageHeader, Row } from "antd";
import { grey1 } from "../../styles/colors";
import { SPACE_LG } from "../../styles/spacing";

const {Content, Sider} = Layout;

/**
 * Standard page layout for pages that have a narrow width layout (e.g. user account details)
 *
 * @param {string} title - page title
 * @param {element} headerExtras - extra information (buttons, search fields, etc.) to display in the page header
 * @param {function} onBack - function to handle on clicking back button
 * @param {string} subTitle - page subtitle
 * @param {element} children - main content for the page
 * @returns {*}
 * @constructor
 */
export function NarrowPageWrapper({
                                    title,
                                    headerExtras,
                                    onBack = undefined,
                                    subTitle = "",
                                    sider,
                                    children
                                  }) {
  return (
    <Layout style={{height: "100%", minHeight: "100%"}}>
      <Row>
        <Col xxl={{span: 12, offset: 6}}
             xl={{span: 20, offset: 2}}
             sm={{span: 22, offset: 1}}>
          <PageHeader
            className="t-main-heading"
            title={title}
            subTitle={subTitle}
            extra={headerExtras}
            onBack={onBack}
          />
          <Layout>
            {sider && (<Sider width={200}
                              style={{backgroundColor: grey1}}>{sider}</Sider>)}
            <Content
              style={{
                backgroundColor: grey1,
                padding: SPACE_LG
              }}>
              {children}
            </Content>
          </Layout>
        </Col>
      </Row>
    </Layout>
  );
}