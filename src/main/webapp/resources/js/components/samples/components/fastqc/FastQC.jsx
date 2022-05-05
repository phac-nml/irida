import { Button, Modal, Space, Typography } from "antd";
import React from "react";
import { FastQCMenu } from "./FastQCMenu";

const { Text } = Typography;
import { IconArrowLeft } from "../../../icons/Icons";
import { clearFastQCData } from "./fastQCSlice";
import { useDispatch, useSelector } from "react-redux";

/**
 * Function to render fastqc modal
 * @returns {JSX.Element}
 * @constructor
 */
export function FastQC() {
  const dispatch = useDispatch();

  const { fileLabel, fastQCModalVisible } = useSelector(
    (state) => state.fastQCReducer
  );

  return (
    <>
      {fastQCModalVisible ? (
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
              <Text level={3} strong>
                {fileLabel}
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
            <FastQCMenu />
          </div>
        </Modal>
      ) : null}
    </>
  );
}
