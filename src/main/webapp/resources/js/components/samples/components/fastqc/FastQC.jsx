import { Button, Modal, Space, Typography } from "antd";
import React from "react";
import { FastQCMenu } from "./FastQCMenu";

const { Text } = Typography;
import { IconArrowLeft } from "../../../icons/Icons";
import { clearFastQCData } from "./fastQCSlice";
import { useDispatch, useSelector } from "react-redux";
import { InfoAlert } from "../../../alerts";
import { ErrorAlert } from "../../../alerts/ErrorAlert";

/**
 * Function to render fastqc modal
 * @returns {JSX.Element}
 * @constructor
 */
export function FastQC() {
  const dispatch = useDispatch();

  const { fileLabel, fastQCModalVisible, processingState } = useSelector(
    (state) => state.fastQCReducer
  );

  const processingStateTranslations = {
    UNPROCESSED: i18n("FastQC.sequencingobject.unprocessed"),
    QUEUED: i18n("FastQC.sequencingobject.queued"),
    PROCESSING: i18n("FastQC.sequencingobject.processing"),
    ERROR: i18n("FastQC.sequencingobject.error"),
  };

  /*
    Gets the processing state
  */
  const getProcessingStateAlert = (processingState) => {
    if (processingState === "ERROR") {
      return (
        <ErrorAlert message={processingStateTranslations[processingState]} />
      );
    } else {
      return (
        <InfoAlert message={processingStateTranslations[processingState]} />
      );
    }
  };

  return (
    <>
      {fastQCModalVisible ? (
        <Modal
          className="t-fastqc-modal"
          bodyStyle={{
            padding: 0,
            maxHeight: window.innerHeight - 400,
            overflowY: "auto",
          }}
          title={
            <Space direction="horizontal" size="small">
              <Button
                type="link"
                onClick={() => dispatch(clearFastQCData())}
                style={{ padding: 0 }}
              >
                <IconArrowLeft />
              </Button>
              <Text level={3} strong>
                <span>{fileLabel}</span>
              </Text>
            </Space>
          }
          visible={fastQCModalVisible}
          footer={null}
          width={800}
          closable={false}
          maskClosable={false}
        >
          <div style={{ margin: 24 }}>
            {processingState === "FINISHED" ? (
              <FastQCMenu />
            ) : (
              getProcessingStateAlert(processingState)
            )}
          </div>
        </Modal>
      ) : null}
    </>
  );
}
