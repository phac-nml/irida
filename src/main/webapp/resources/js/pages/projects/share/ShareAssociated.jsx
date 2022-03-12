import React from "react";
import { Alert, Collapse, List, Button } from "antd";
import { useSelector } from "react-redux";
import { grey1 } from "../../../styles/colors";
import { SampleDetailViewer } from "../../../components/samples/SampleDetailViewer";
import { setBaseUrl } from "../../../utilities/url-utilities";

/**
 * React showing which sample belong to an associated project and cannot be moved or shared
 * @returns {JSX.Element}
 */
export default function ShareAssociated() {
  const { associated } = useSelector((state) => state.shareReducer);

  return (
    <Alert
      type="error"
      message={
        <Collapse ghost>
          <Collapse.Panel
            header={
              associated.length === 1
                ? i18n("ShareAssociated.header-singular")
                : i18n("ShareAssociated.header-plural", associated.length)
            }
          >
            <List
              style={{ backgroundColor: grey1 }}
              bordered
              size="small"
              dataSource={associated}
              renderItem={(sample) => (
                <List.Item
                  extra={
                    <Button
                      type="link"
                      href={setBaseUrl(`projects/${sample.projectId}`)}
                    >
                      {sample.projectName}
                    </Button>
                  }
                >
                  <SampleDetailViewer
                    sampleId={sample.id}
                    projectId={sample.projectId}
                  >
                    <Button size="small">{sample.name}</Button>
                  </SampleDetailViewer>
                </List.Item>
              )}
            />
          </Collapse.Panel>
        </Collapse>
      }
    />
  );
}
