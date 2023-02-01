import {
  Alert,
  Checkbox,
  Col,
  Form,
  Input,
  Modal,
  notification,
  Radio,
  Row,
  Space,
  Typography,
} from "antd";
import React from "react";
import { useMergeMutation } from "../../../../apis/projects/samples";
import type { StoredSample } from "../../../../types/irida";
import { serverValidateSampleName } from "../../../../utilities/validation-utilities";
import { useTypedSelector } from "../store";
import LockedSamplesList from "./LockedSamplesList";

type MergeModalProps = {
  onCancel: () => void;
  onComplete: () => void;
  samples: { locked: StoredSample[]; valid: StoredSample[] };
  visible: boolean;
};

/**
 * React element to display a modal to merge multiple samples into a single one.
 * @param samples - list of samples to merge together
 * @param visible - whether the modal is currently visible on the page
 * @param onComplete - function to call when the merge is complete
 * @param onCancel - function to call when the merge is cancelled.
s */
export default function MergeModal({
  samples,
  visible,
  onComplete,
  onCancel,
}: MergeModalProps) {
  const { projectId } = useTypedSelector((state) => state.samples);
  const [merge, { isLoading }] = useMergeMutation();

  const [renameSample, setRenameSample] = React.useState(false);
  const [error, setError] = React.useState(undefined);
  const [form] = Form.useForm();

  const initialValues = {
    primary: samples.valid[0]?.id,
    newName: "",
  };

  React.useEffect(() => {
    if (!renameSample) {
      form.setFieldsValue({
        newName: "",
      });
    }
  }, [form, renameSample]);

  // Server validate new name
  const validateName = async (name: string) => {
    if (renameSample) {
      return serverValidateSampleName(name);
    }
    return Promise.resolve();
  };

  const onSubmit = async () => {
    try {
      const { newName, primary }: typeof initialValues =
        await form.validateFields();

      const ids = samples.valid
        .map((sample) => sample.id)
        .filter((id) => id !== primary);

      const { message } = await merge({
        projectId: Number(projectId),
        body: {
          newName,
          primary,
          ids,
        },
      }).unwrap();

      notification.success({
        message: i18n("MergeModal.success"),
        description: message,
      });
      onComplete();
    } catch {
      /*
      If the form is in an invalid state it will hit here.  This will prevent the
      invalid date from being submitted and display the errors (if not already displayed)
       to the user.
       */
    }
  };

  return (
    <Modal
      title={i18n("MergeModal.title")}
      className="t-merge-modal"
      visible={visible}
      onOk={onSubmit}
      okText={i18n("MergeModal.okText")}
      okButtonProps={{
        loading: isLoading,
      }}
      onCancel={onCancel}
      cancelText={i18n("MergeModal.cancelText")}
      width={600}
    >
      <Row gutter={[16, 16]}>
        {samples.valid.length >= 2 ? (
          <>
            <Col span={24}>
              <Alert
                type="warning"
                showIcon
                message={i18n("MergeModal.metadata-warning")}
              />
            </Col>
            {error !== undefined ? (
              <Col span={24}>
                <Alert
                  showIcon
                  type="error"
                  message={error}
                  closable
                  onClose={() => setError(undefined)}
                />
              </Col>
            ) : null}
            <Col span={24}>
              <Form form={form} layout="vertical" initialValues={initialValues}>
                <Form.Item
                  name="primary"
                  label={i18n("MergeModal.input-primary")}
                  required
                >
                  <Radio.Group>
                    <Space direction="vertical">
                      {samples.valid.map((sample) => {
                        return (
                          <Radio value={sample.id} key={`sample-${sample.id}`}>
                            {sample.sampleName}
                          </Radio>
                        );
                      })}
                    </Space>
                  </Radio.Group>
                </Form.Item>
                <Form.Item noStyle>
                  <Checkbox
                    className="t-custom-checkbox"
                    checked={renameSample}
                    onChange={(e) => setRenameSample(e.target.checked)}
                  >
                    {i18n("MergeModal.rename")}
                  </Checkbox>
                  <Form.Item
                    name="newName"
                    rules={[
                      () => ({
                        validator(_, value) {
                          return validateName(value);
                        },
                      }),
                    ]}
                  >
                    <Input disabled={!renameSample} />
                  </Form.Item>
                </Form.Item>
              </Form>
            </Col>
          </>
        ) : (
          <Col span={24}>
            <Alert
              type="error"
              showIcon
              message={i18n("MergeModal.error-valid")}
            />
          </Col>
        )}
        {samples.locked.length ? (
          <Col span={24}>
            <Typography.Text strong>
              {i18n("LockedSamplesList.header")}
            </Typography.Text>
            <LockedSamplesList locked={samples.locked} />
          </Col>
        ) : null}
      </Row>
    </Modal>
  );
}
