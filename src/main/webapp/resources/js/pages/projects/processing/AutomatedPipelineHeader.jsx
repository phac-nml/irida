import React from "react";
import { Button, List, Modal, Typography } from "antd";
import { fetchAutomatedIridaAnalysisWorkflows } from "../../../apis/pipelines/pipelines";
import { setBaseUrl } from "../../../utilities/url-utilities";

export function AutomatedPipelineHeader({ projectId }) {
  const [visible, setVisible] = React.useState(false);
  const [pipelines, setPipelines] = React.useState([]);

  React.useEffect(() => {
    fetchAutomatedIridaAnalysisWorkflows().then(setPipelines);
  }, []);

  return (
    <>
      <div style={{ display: "flex", justifyContent: "space-between" }}>
        <Typography.Title level={3}>
          {i18n("AddPipelineModal.title")}
        </Typography.Title>
        <Button onClick={() => setVisible(true)}>
          {i18n("AddPipelineModal.button")}
        </Button>
      </div>
      <Modal
        visible={visible}
        title={`Select an Automated Pipeline`}
        onCancel={() => setVisible(false)}
      >
        <List
          bordered
          dataSource={pipelines}
          renderItem={(item) => (
            <List.Item key={item.id}>
              <List.Item.Meta
                title={item.name}
                description={item.description}
              />
              <Button
                href={setBaseUrl(
                  `/launch?id=${item.id}&projectId=${projectId}`
                )}
              >
                Add Pipeline
              </Button>
            </List.Item>
          )}
        />
      </Modal>
    </>
  );
}
