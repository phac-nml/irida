import React from "react";
import { Col, List, Modal, Row, Space, Typography } from "antd";
import { useRemoveMutation } from "../services/samples";
import { LockOutlined } from "@ant-design/icons";
import LockedSamplesTable from "./LockedSamplesTable";

export default function RemoveModal({
  samples,
  visible,
  onComplete,
  onCancel,
}) {
  const [removeSamples, { isLoading }] = useRemoveMutation();

  const onOk = async () => {
    try {
      const response = await removeSamples(
        samples.valid.map((sample) => sample.id)
      );
      onComplete();
    } catch (e) {
      console.log(e);
    }
  };

  return (
    <Modal
      title={"REMOVE SAMPLES FROM PROJECT"}
      visible={visible}
      onCancel={onCancel}
      onOk={onOk}
      okButtonProps={{
        loading: isLoading,
      }}
      width={600}
    >
      <Row gutter={[16, 16]}>
        <Col span={24}>
          <List
            size="small"
            bordered
            header={<Typography.Text>Samples to be removed</Typography.Text>}
            dataSource={samples.valid}
            renderItem={(sample) => (
              <List.Item>
                <List.Item.Meta title={sample.sampleName} />
              </List.Item>
            )}
          />
        </Col>
        {samples.locked.length > 0 && (
          <Col span={24}>
            <LockedSamplesTable locked={samples.locked} />
          </Col>
        )}
        {samples.associated.length > 0 && (
          <Col span={24}>
            <List
              size="small"
              bordered
              header={
                <Space>
                  <LockOutlined />
                  <Typography.Text>
                    These samples are from an associated project and cannot be
                    removed from there
                  </Typography.Text>
                </Space>
              }
              dataSource={samples.associated}
              renderItem={(sample) => (
                <List.Item>
                  <List.Item.Meta title={sample.sampleName} />
                </List.Item>
              )}
            />
          </Col>
        )}
      </Row>
    </Modal>
  );
}
