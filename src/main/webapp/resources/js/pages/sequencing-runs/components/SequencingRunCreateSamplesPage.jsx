import React from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { DnDCard } from "./example/DnDCard";
import { useGetSequencingRunFilesQuery } from "../../../apis/sequencing-runs/sequencing-runs";
import { useNavigate, useParams } from "react-router-dom";
import { Col, List, Row, Typography } from "antd";
import { AddNewButton } from "../../../components/Buttons/AddNewButton";
import { DnDList } from "./example/DnDList";
import { SequencingRunSamplesList } from "./SequencingRunSamplesList";

/**
 * React component to display page that creates samples from a sequencing run.
 * @returns {*}
 */
export default function SequencingRunCreateSamplesPage() {
  const { runId } = useParams();
  const navigate = useNavigate();
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
        list: "samplesList" + samples.length,
      },
      ...samples,
    ]);
  };

  return (
    <PageWrapper
      title={i18n("SequencingRunCreateSamplesPage.title", runId)}
      onBack={() => navigate(-1)}
      headerExtras={
        <AddNewButton
          onClick={addNewSample}
          text={i18n("SequencingRunCreateSamplesPage.samples.button")}
        />
      }
    >
      <DndProvider backend={HTML5Backend}>
        <Row gutter={32}>
          <Col span={12}>
            <Typography.Title level={5}>
              {i18n("SequencingRunCreateSamplesPage.samples.title")}
            </Typography.Title>
            <SequencingRunSamplesList
              samples={samples}
              returnItemsForList={returnItemsForList}
              setFiles={setFiles}
            />
          </Col>
          <Col span={12}>
            <Typography.Title level={5}>
              {i18n("SequencingRunCreateSamplesPage.files.title")}
            </Typography.Title>
            <DnDList
              name="filesList"
              emptyDescription={i18n("SequencingRunCreateSamplesPage.empty")}
              grid={{ column: 1 }}
              dataSource={returnItemsForList("filesList")}
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
