import { Button, Modal, Space, Typography } from "antd";
import React from "react";
import { FastQCMenu } from "./FastQCMenu";

const { Text } = Typography;
import { IconArrowLeft } from "../../../icons/Icons";
import { clearFastQCData, setFastQCDetails } from "./fastQCSlice";
import { RootStateOrAny, useDispatch, useSelector } from "react-redux";
import { getFastQCDetails } from "../../../../apis/files/sequence-files";
import { InfoAlert } from "../../../alerts";

const qcEntryTranslations: { [key: string]: string } = {
  UNPROCESSED: i18n("FastQC.sequencingobject.UNPROCESSED"),
  QUEUED: i18n("FastQC.sequencingobject.QUEUED"),
  ERRORED: i18n("FastQC.sequencingobject.ERRORED"),
  PROCESSING: i18n("FastQC.sequencingobject.PROCESSING"),
};

/**
 * Function to render fastqc modal
 * @returns {JSX.Element}
 * @constructor
 */
export function FastQC() {
  const dispatch = useDispatch();

  const {
    fileLabel,
    fastQCModalVisible,
    sequencingObjectId,
    fileId,
    fastQC,
    processingState,
  } = useSelector((state: RootStateOrAny) => state.fastQCReducer);

  React.useEffect(() => {
    getFastQCDetails(sequencingObjectId, fileId).then(
      ({ analysisFastQC, sequenceFile, sequencingObject }) => {
        dispatch(
          setFastQCDetails({
            fastQC: analysisFastQC,
            file: sequenceFile,
            processingState: sequencingObject.processingState,
          })
        );
      }
    );
  }, [sequencingObjectId, fileId]);

  const textLevel = {
    level: 3,
  };

  return (
    <Modal
      className="t-fastqc-modal"
      mask={false}
      bodyStyle={{
        padding: 0,
        height: 800,
        overflowY: "auto",
      }}
      title={
        <Space direction="horizontal" size="small">
          <Button
            type="text"
            onClick={() => dispatch(clearFastQCData())}
            className="t-fastqc-modal-close"
            icon={<IconArrowLeft />}
          />
          <Text {...textLevel} strong className="t-fastqc-modal-file-name">
            {fileLabel}
          </Text>
        </Space>
      }
      visible={fastQCModalVisible}
      footer={null}
      width={900}
      closable={false}
      maskClosable={false}
    >
      <div style={{ margin: 24 }}>
        {fastQC !== null ? (
          <FastQCMenu />
        ) : fastQC === null && processingState !== "FINISHED" ? (
          <InfoAlert message={qcEntryTranslations[processingState]} />
        ) : (
          <InfoAlert
            message={i18n("FastQC.sequencingobject.finishedNoResults")}
          />
        )}
      </div>
    </Modal>
  );
}
