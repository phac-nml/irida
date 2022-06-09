import React from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { DnDCard } from "./example/DnDCard";
import { useGetSequencingRunFilesQuery } from "../../../apis/sequencing-runs/sequencing-runs";
import { useParams } from "react-router-dom";
import { Card, Col, List, Row, Typography } from "antd";
import { AddNewButton } from "../../../components/Buttons/AddNewButton";
import { DnDList } from "./example/DnDList";

/**
 * React component to display page that creates samples from a sequencing run.
 * @returns {*}
 */
export function SequencingRunCreateSamples() {
  const { runId } = useParams();
  const { data = [] } = useGetSequencingRunFilesQuery(runId);
  const [files, setFiles] = React.useState([]);
  const [samples, setSamples] = React.useState([]);

  React.useEffect(() => {
    setFiles(data);
  }, [data]);

  const returnItemsForList = (listName) => {
    return files.filter((item) => item.list === listName);
  };

  const addNewSample = () => {
    setSamples([
      {
        sampleName: "Sample " + samples.length,
        list: "New Sample List " + samples.length,
      },
      ...samples,
    ]);
  };

  return (
    <PageWrapper title={i18n("SequencingRunCreateSamples.title")}>
      <DndProvider backend={HTML5Backend}>
        <Row gutter={32}>
          <Col span={12}>
            <Typography.Title level={5}>
              {i18n("SequencingRunCreateSamples.samples.title")}
            </Typography.Title>
            <AddNewButton
              onClick={addNewSample}
              text={i18n("SequencingRunCreateSamples.samples.button")}
            />
            <List
              grid={{ column: 1 }}
              dataSource={samples}
              renderItem={(item) => (
                <List.Item>
                  <Card title={item.sampleName}>
                    <DnDList
                      name={item.list}
                      grid={{ column: 2 }}
                      dataSource={returnItemsForList(item.list)}
                      renderItem={(item) => (
                        <List.Item>
                          <DnDCard id={item.id} setList={setFiles}>
                            {item.fileName}
                          </DnDCard>
                        </List.Item>
                      )}
                    />
                  </Card>
                </List.Item>
              )}
            />
          </Col>
          <Col span={12}>
            <Typography.Title level={5}>
              {i18n("SequencingRunCreateSamples.files.title")}
            </Typography.Title>
            <DnDList
              name="filesList"
              grid={{ column: 1 }}
              dataSource={returnItemsForList("Sequencing File List")}
              renderItem={(item) => (
                <List.Item>
                  <DnDCard id={item.id} setList={setFiles}>
                    {item.fileName}
                  </DnDCard>
                </List.Item>
              )}
            />
          </Col>
        </Row>
      </DndProvider>
    </PageWrapper>
  );
}
