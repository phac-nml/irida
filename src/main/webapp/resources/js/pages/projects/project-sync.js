import React from "react";
import { createRoot } from "react-dom/client";
import { Card, Col, Row } from "antd";
import { SPACE_LG } from "../../styles/spacing";
import { CreateRemoteProjectSyncForm } from "../../components/remote-api/CreateRemoteProjectSyncForm";

function NewRemoteProjectForm() {
  return (
    <Row style={{ marginTop: SPACE_LG }}>
      <Col md={{ span: 12, offset: 6 }}>
        <Card title={i18n("NewProjectSync.title")}>
          <CreateRemoteProjectSyncForm />
        </Card>
      </Col>
    </Row>
  );
}

const root = createRoot(document.querySelector("#root"));
root.render(<NewRemoteProjectForm />);
