import React from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { DndProvider } from "react-dnd";
import { HTML5Backend } from "react-dnd-html5-backend";
import { useGetSequencingRunFilesQuery } from "../../../apis/sequencing-runs/sequencing-runs";
import { useNavigate, useParams } from "react-router-dom";
import { Col, Row, Typography } from "antd";
import { AddNewButton } from "../../../components/Buttons/AddNewButton";
import { SequencingRunSamplesList } from "./SequencingRunSamplesList";
import { SequencingRunFilesList } from "./SequencingRunFilesList";
import { useDispatch, useSelector } from "react-redux";
import { addSample, setFiles } from "../services/runReducer";

/**
 * React component to display page that creates samples from a sequencing run.
 * @returns {*}
 * @constructor
 */
export default function SequencingRunCreateSamplesPage() {
  const { runId } = useParams();
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const { data = [] } = useGetSequencingRunFilesQuery(runId);

  React.useEffect(() => {
    dispatch(setFiles(data));
  }, [data]);

  const { files, samples } = useSelector((state) => state.reducer);

  const addNewSample = () => {
    dispatch(
      addSample({
        sampleName: "New Sample",
        forwardSequenceFile: null,
        reverseSequenceFile: null,
      })
    );
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
          <Col span={16}>
            <Typography.Title level={5}>
              {i18n("SequencingRunCreateSamplesPage.samples.title")}
            </Typography.Title>
            <SequencingRunSamplesList samples={samples} />
          </Col>
          <Col span={8}>
            <Typography.Title level={5}>
              {i18n("SequencingRunCreateSamplesPage.files.title")}
            </Typography.Title>
            <SequencingRunFilesList
              samples={samples}
              files={files.filter((item) => item.show)}
            />
          </Col>
        </Row>
      </DndProvider>
    </PageWrapper>
  );
}
