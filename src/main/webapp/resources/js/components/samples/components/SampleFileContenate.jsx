import {
  Avatar,
  Form,
  Input,
  List,
  Modal,
  notification,
  Radio,
  Space,
  Typography,
} from "antd";
import React from "react";
import { useDispatch, useSelector } from "react-redux";
import { useResetFormOnCloseModal } from "../../../hooks";
import { useConcatenateSequencingObjectsMutation } from "../../../apis/samples/samples";
import {
  addToSequenceFiles,
  removeFileObjectFromSample,
  resetConcatenateSelected,
} from "../sampleFilesSlice";
import { IconArrowLeft, IconArrowRight, IconFile } from "../../icons/Icons";

const { Title } = Typography;

/**
 * Function to render Sequencing Object concatenation modal
 * @param children
 * @returns {JSX.Element}
 * @constructor
 */
export function SampleFileConcatenate({ children }) {
  const [visible, setVisible] = React.useState(false);
  const dispatch = useDispatch();
  const [form] = Form.useForm();
  const { sample } = useSelector((state) => state.sampleReducer);
  const { concatenateSelected } = useSelector(
    (state) => state.sampleFilesReducer
  );
  const [concatenateSeqObjectFiles] = useConcatenateSequencingObjectsMutation();

  useResetFormOnCloseModal({
    form,
    visible,
  });

  const concatenateFiles = () => {
    let sequencingObjectIds = concatenateSelected.map(
      (seqObject) => seqObject.identifier
    );

    form.validateFields().then((values) => {
      concatenateSeqObjectFiles({
        sampleId: sample.identifier,
        sequencingObjectIds: sequencingObjectIds,
        newFileName: values.new_file_name,
        removeOriginals: values.remove_original_files,
      })
        .then(({ data }) => {
          let message = i18n("SampleFilesConcatenate.concatenationSuccess");

          if (values.remove_original_files) {
            message = i18n("SampleFilesConcatenate.concatenationRemoveSuccess");
            sequencingObjectIds.map((seqObjId) => {
              dispatch(
                removeFileObjectFromSample({
                  fileObjectId: seqObjId,
                  type: "sequencingObject",
                })
              );
            });
          }

          dispatch(resetConcatenateSelected({}));
          dispatch(addToSequenceFiles({ sequenceFiles: data }));
          notification.success({ message });
          form.resetFields();
          setVisible(false);
        })
        .catch((error) => {
          notification.error({
            message: error,
          });
        });
    });
  };

  /*
   Check if files selected for concatenation
   are of the same type or not
   */
  const validateFileTypes = () => {
    const numFilesSelected = concatenateSelected.length;
    let pairedCount = 0;
    let singleEndCount = 0;

    concatenateSelected.map((selected) => {
      if (selected.files !== undefined) {
        pairedCount++;
      } else if (selected.sequenceFile !== undefined) {
        singleEndCount++;
      }
    });

    if (pairedCount === numFilesSelected) return true;
    if (singleEndCount === numFilesSelected) return true;
    return false;
  };

  return (
    <>
      {React.cloneElement(children, {
        onClick: () => {
          let fileTypesValid = validateFileTypes();
          if (fileTypesValid) {
            setVisible(true);
          } else {
            notification.warning({
              message: i18n("SampleFilesConcatenate.mixedFileTypesWarning"),
              duration: 10,
            });
            dispatch(resetConcatenateSelected({}));
          }
        },
      })}
      {visible ? (
        <Modal
          className="t-concatenate-confirm-modal"
          onCancel={() => {
            dispatch(resetConcatenateSelected({}));
            setVisible(false);
          }}
          visible={visible}
          onOk={concatenateFiles}
          okText={i18n("SampleFilesConcatenate.okText")}
          cancelText={i18n("SampleFilesConcatenate.cancelText")}
          okButtonProps={{ className: "t-concatenate-confirm" }}
          cancelButtonProps={{ className: "t-concatenate-cancel" }}
        >
          <Space size="large" direction="vertical" style={{ width: `100%` }}>
            <Title level={4}>{i18n("SampleFilesConcatenate.title")}</Title>

            <List
              bordered
              layout={`vertical`}
              dataSource={concatenateSelected}
              renderItem={(seqObject) => {
                if (seqObject.files !== undefined) {
                  return (
                    <>
                      <List.Item
                        key={`seqObject-${seqObject.forwardSequenceFile.identifier}`}
                        style={{ width: `100%` }}
                        className="t-pair-forward-sequence-file"
                      >
                        <List.Item.Meta
                          avatar={
                            <Avatar size={`small`} icon={<IconArrowRight />} />
                          }
                          title={seqObject.forwardSequenceFile.label}
                        />
                      </List.Item>
                      <List.Item
                        key={`seqObject-${seqObject.reverseSequenceFile.identifier}`}
                        style={{ width: `100%` }}
                        className="t-pair-reverse-sequence-file"
                      >
                        <List.Item.Meta
                          avatar={
                            <Avatar size={`small`} icon={<IconArrowLeft />} />
                          }
                          title={seqObject.reverseSequenceFile.label}
                        />
                      </List.Item>
                    </>
                  );
                } else {
                  return (
                    <List.Item
                      key={`seqObject-${seqObject.identifier}`}
                      style={{ width: `100%` }}
                      className="t-single-end-file"
                    >
                      <List.Item.Meta
                        avatar={<Avatar size={`small`} icon={<IconFile />} />}
                        title={seqObject.label}
                      />
                    </List.Item>
                  );
                }
              }}
            />

            <Form layout="vertical" form={form}>
              <Form.Item
                key="new-file-name"
                name="new_file_name"
                label={i18n("SampleFilesConcatenate.newFileName")}
                rules={[
                  {
                    required: true,
                    message: (
                      <div className="t-new-file-name-required">
                        {i18n("SampleFilesConcatenate.nameValidationError")}
                      </div>
                    ),
                  },
                ]}
              >
                <Input id="t-concat-new-file-name" />
              </Form.Item>

              <Form.Item
                key="remove-original-files"
                name="remove_original_files"
                label={i18n("SampleFilesConcatenate.removeOriginalFiles")}
                initialValue={false}
              >
                <Radio.Group optionType="button" buttonStyle="solid">
                  <div
                    style={{ display: "flex", justifyContent: "space-between" }}
                  >
                    <Radio.Button
                      value={false}
                      className="t-remove-originals-false"
                    >
                      {i18n("SampleFilesConcatenate.keepOriginals")}
                    </Radio.Button>
                    <Radio.Button
                      value={true}
                      className="t-remove-originals-true"
                    >
                      {i18n("SampleFilesConcatenate.removeOriginals")}
                    </Radio.Button>
                  </div>
                </Radio.Group>
              </Form.Item>
            </Form>
          </Space>
        </Modal>
      ) : null}
    </>
  );
}
