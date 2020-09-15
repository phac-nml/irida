import React from "react";
import { render } from "react-dom";
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

render(<NewRemoteProjectForm />, document.querySelector("#root"));
