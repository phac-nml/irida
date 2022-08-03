import React from "react";
import { Col, Form, Input, List, Modal, Row, Typography } from "antd";
import { useSelector } from "react-redux";
import { CheckCircleTwoTone, WarningTwoTone } from "@ant-design/icons";
import { green6, red6 } from "../../../../styles/colors";
import VirtualList from "rc-virtual-list";
import { SPACE_SM } from "../../../../styles/spacing";
import { setBaseUrl } from "../../../../utilities/url-utilities";

const ROW_HEIGHT = 43;

/**
 * React component to render a modal to allow the user to select a file containing comma or new
 * line seperated sample names to be used to filter the samples' table by.
 *
 * @param {boolean} visible - whether the modal is currently visible
 * @param {function} onComplete - function to call when the sample names are available
 * @param {function} onCancel - function to call when cancel the modal
 * @returns {JSX.Element}
 * @constructor
 */
export default function FilterByFileModal({ visible, onComplete, onCancel }) {
  const { options, projectId } = useSelector((state) => state.samples);
  const [contents, setContents] = React.useState("");
  const [filename, setFilename] = React.useState("");
  const [valid, setValid] = React.useState([]);
  const [invalid, setInvalid] = React.useState([]);

  const onFileAdded = async (e) => {
    const [file] = e.target.files;
    setFilename(file.name);

    const fileContent = await file.text();
    setContents(fileContent);
  };

  React.useEffect(() => {
    if (contents.length) {
      const associated = options.filters.associated || [];
      // Split the contents of the file on either new line or coma, and filter empty entries.
      let parsed = contents.split(/[\s,]+/).filter(Boolean);
      const projectIds = [projectId, ...associated];

      fetch(setBaseUrl(`/ajax/samples/validate`), {
        headers: {
          Accept: "application/json",
          "Content-Type": "application/json",
        },
        method: "POST",
        body: JSON.stringify({
          projectIds,
          names: parsed,
        }),
      })
        .then((response) => response.json())
        .then(({ valid, invalid }) => {
          setValid(valid);
          setInvalid(invalid);
        });
    } else {
      setValid([]);
      setInvalid([]);
    }
  }, [contents, options.filters.associated, projectId]);

  const onOk = () => {
    onComplete({ samples: valid, filename });
  };

  return (
    <Modal
      visible={visible}
      onCancel={onCancel}
      onOk={onOk}
      okButtonProps={{ disabled: valid.length === 0 }}
      okText={i18n("FilterByFile.filter")}
      width={600}
    >
      <>
        <Form layout="vertical">
          <Form.Item label={i18n("FilterByFile.file-label")}>
            <Input
              type="file"
              onChange={onFileAdded}
              className="t-filter-by-file-input"
            />
          </Form.Item>
        </Form>
        <Row gutter={[16, 16]}>
          {valid.length > 0 && (
            <Col span={24}>
              <Typography.Text>
                <Row align="middle">
                  <CheckCircleTwoTone
                    twoToneColor={green6}
                    style={{ fontSize: `2rem`, marginRight: SPACE_SM }}
                  />
                  {valid.length === 1
                    ? i18n("FilterByFile.valid.single")
                    : i18n("FilterByFile.valid.plural", valid.length)}
                </Row>
              </Typography.Text>
            </Col>
          )}
          {invalid.length > 0 && (
            <Col span={24}>
              <Row gutter={[8, 8]}>
                <Col span={24}>
                  <Row align="middle">
                    <WarningTwoTone
                      style={{ fontSize: `2rem`, marginRight: SPACE_SM }}
                      twoToneColor={red6}
                    />
                    {i18n("FilterByFile.invalid")}
                  </Row>
                </Col>
                <Col span={24}>
                  <List bordered size="small">
                    <VirtualList
                      data={invalid}
                      height={Math.min(400, ROW_HEIGHT * invalid.length)}
                      itemHeight={ROW_HEIGHT}
                      itemKey={(item) => {
                        return item;
                      }}
                    >
                      {(item) => (
                        <List.Item key={item}>
                          <List.Item.Meta
                            title={
                              <span className="t-invalid-sample">{item}</span>
                            }
                          />
                        </List.Item>
                      )}
                    </VirtualList>
                  </List>
                </Col>
              </Row>
            </Col>
          )}
        </Row>
      </>
    </Modal>
  );
}
