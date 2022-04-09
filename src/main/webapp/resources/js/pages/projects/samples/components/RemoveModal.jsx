import React from "react";
import { Col, Divider, List, Modal, Row, Space, Typography } from "antd";
import { useRemoveMutation } from "../services/samples";
import { LockOutlined } from "@ant-design/icons";
import LockedSamplesTable from "./LockedSamplesTable";

/**
 * React Element to display a modal with sample to be removed from the current
 * project.
 *  - Will display samples that are locked - cannot be removed.
 *  - Will display associated samples that cannot be removed from this project.
 * @param samples
 * @param visible
 * @param onComplete
 * @param onCancel
 * @returns {JSX.Element}
 * @constructor
 */
export default function RemoveModal({
  samples,
  visible,
  onComplete,
  onCancel
}) {
  const [removeSamples, { isLoading }] = useRemoveMutation();

  const onOk = async () => {
    try {
      const response = await removeSamples(
        samples.valid.map(sample => sample.id)
      );
      onComplete();
    } catch (e) {
      console.log(e);
    }
  };

  return (
    <Modal
      title={i18n("RemoveModal.title")}
      visible={visible}
      onCancel={onCancel}
      onOk={onOk}
      okText={i18n("RemoveModal.okText")}
      okButtonProps={{
        loading: isLoading
      }}
      width={600}
    >
      <Row gutter={[16, 16]}>
        <Col span={24}>
          <List
            size="small"
            bordered
            header={
              <Typography.Text>{i18n("RemoveModal.valid")}</Typography.Text>
            }
            dataSource={samples.valid}
            renderItem={sample => (
              <List.Item>
                <List.Item.Meta title={sample.sampleName} />
              </List.Item>
            )}
          />
        </Col>
        {(samples.locked.length > 0 || samples.associated.length > 0) && (
          <Col span={24}>
            <Divider orientation="left" plain>
              {i18n("RemoveModal.divider")}
            </Divider>
          </Col>
        )}
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
                    {i18n("RemoveModal.associated")}
                  </Typography.Text>
                </Space>
              }
              dataSource={samples.associated}
              renderItem={sample => (
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
