import React from "react";
import { Col, List, Modal, Row, Typography } from "antd";
import { useRemoveMutation } from "../services/samples";

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
            header={<Typography.Text>Sample to be removed</Typography.Text>}
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
            <List
              size="small"
              bordered
              header={
                <Typography.Text>
                  You do not have permission to modify these samples
                </Typography.Text>
              }
              dataSource={samples.locked}
              renderItem={(sample) => (
                <List.Item>
                  <List.Item.Meta title={sample.sampleName} />
                </List.Item>
              )}
            />
          </Col>
        )}
        {samples.associated.length > 0 && (
          <Col span={24}>
            <List
              size="small"
              bordered
              header={
                <Typography.Text>
                  These samples are from an associated project and cannot be
                  removed from there
                </Typography.Text>
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
