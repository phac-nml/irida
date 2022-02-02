import {
  Form,
  Input,
  List,
  Modal,
  notification,
  Select,
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
} from "../sampleFilesSlice";
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
        .then((response) => {
          let message = "Successfully concatenated files";

          if (values.remove_original_files) {
            message =
              "Successfully concatenated files and removed the originals.";
            sequencingObjectIds.map((seqObjId) => {
              dispatch(
                removeFileObjectFromSample({
                  fileObjectId: seqObjId,
                  type: "sequencingObject",
                })
              );
            });
          }
          // clear selected items. Might need to move concatenateSelected array to sampleSlice from sampleFileSlice

          dispatch(addToSequenceFiles({ sequenceFiles: response.data }));
          notification.success({ message });
          form.resetFields();
          setVisible(false);
        })
        .catch((error) => {
          notification.error({
            message: "There was an error concatenating the files",
          });
        });
    });
  };

  return (
    <>
      {React.cloneElement(children, {
        onClick: () => setVisible(true),
      })}
      {visible ? (
        <Modal
          className="t-concatenate-confirm"
          onCancel={() => setVisible(false)}
          visible={visible}
          onOk={concatenateFiles}
          okText="Cocatenate"
          cancelText="Cancel"
        >
          <Space size="small" direction="vertical" style={{ width: `100%` }}>
            <Title level={4}>
              Concatenate the following sequencing objects
            </Title>

            <List
              bordered
              layout={`vertical`}
              dataSource={concatenateSelected}
              renderItem={(seqObject) => {
                return (
                  <List.Item
                    key={`seqObject-${seqObject.identifier}`}
                    style={{ width: `100%` }}
                  >
                    <List.Item.Meta title={seqObject.label} />
                  </List.Item>
                );
              }}
            />

            <Form layout="vertical" form={form}>
              <Form.Item
                name="new_file_name"
                label="New file name:"
                rules={[
                  {
                    required: true,
                    message: (
                      <div className="t-new-file-name-required">
                        A file name must be provided
                      </div>
                    ),
                  },
                ]}
              >
                <Input />
              </Form.Item>
              <Form.Item
                name="remove_original_files"
                label="Would you like to remove the original files?"
                initialValue={false}
              >
                <Select style={{ width: "100%" }}>
                  <Select.Option
                    className={`t-remove-originals`}
                    value={true}
                    key={"t-remove-originals-true"}
                  >
                    Remove Originals
                  </Select.Option>
                  <Select.Option
                    className={`t-keep-originals`}
                    value={false}
                    key={"t-remove-originals-false"}
                  >
                    Keep Originals
                  </Select.Option>
                  ))}
                </Select>
              </Form.Item>
            </Form>
          </Space>
        </Modal>
      ) : null}
    </>
  );
}
