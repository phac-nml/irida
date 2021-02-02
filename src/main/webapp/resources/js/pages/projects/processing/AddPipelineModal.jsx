import React from "react";
import { Button, List, Modal } from "antd";
import { useVisibility } from "../../../contexts/visibility-context";
import { fetchAutomatedIridaAnalysisWorkflows } from "../../../apis/pipelines/pipelines";
import { setBaseUrl } from "../../../utilities/url-utilities";

export function AddPipelineModal({ projectId }) {
  const [visible, setVisible] = useVisibility();
  const [pipelines, setPipelines] = React.useState([]);

  React.useEffect(() => {
    fetchAutomatedIridaAnalysisWorkflows().then(setPipelines);
  }, []);

  return (
    <section>
      <Button onClick={() => setVisible(true)}>Add Automated Pipeline</Button>
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
    </section>
  );
}
