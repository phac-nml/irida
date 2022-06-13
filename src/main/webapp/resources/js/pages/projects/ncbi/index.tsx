import { Col, PageHeader, Row } from "antd";
import React from "react";
import { render } from "react-dom";
import { Outlet, useNavigate, useParams } from "react-router-dom";
import ProjectSPA from "../ProjectSPA";

/**
 * Render NCBI Export listing for a project page.
 * @returns {JSX.Element}
 * @constructor
 */
export default function ProjectNCBILayout() {
  const { id: submissionId } = useParams();
  const navigate = useNavigate();

  return (
    <Row gutter={[16, 16]}>
      <Col
        xxl={{ span: 12, offset: 6 }}
        xl={{ span: 20, offset: 2 }}
        sm={{ span: 22, offset: 1 }}
      >
        <PageHeader
          onBack={submissionId ? () => navigate(-1) : undefined}
          title={i18n("NcbiExportPage.title")}
        />
        <Outlet />
      </Col>
    </Row>
  );
}

// TODO: This will need to be moved up as the project SPA gets created.
render(<ProjectSPA />, document.querySelector("#root"));
