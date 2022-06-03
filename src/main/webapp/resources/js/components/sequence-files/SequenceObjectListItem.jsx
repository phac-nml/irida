import React from "react";
import { Avatar, Checkbox, List, Tooltip } from "antd";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import {
  FileOutlined,
  SwapOutlined,
  SwapRightOutlined,
} from "@ant-design/icons";
import { blue6 } from "../../styles/colors";
import { primaryColour } from "../../utilities/theme-utilities";
import { SPACE_XS } from "../../styles/spacing";
import {
  addToConcatenateSelected,
  removeFromConcatenateSelected,
} from "../samples/sampleFilesSlice";
import { useDispatch } from "react-redux";

export function SequenceObjectListItem({
  sequenceObject,
  actions = [],
  displayConcatenationCheckbox = false,
}) {
  const obj = sequenceObject.fileInfo
    ? sequenceObject.fileInfo
    : sequenceObject.file
    ? sequenceObject.file
    : sequenceObject;

  const { files, sequenceFile, file } = obj;

  const dispatch = useDispatch();

  const updateSelected = (e) => {
    if (e.target.checked) {
      dispatch(addToConcatenateSelected({ seqObject: obj }));
    } else {
      dispatch(removeFromConcatenateSelected({ seqObject: obj }));
    }
  };
  return (
    <List.Item>
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          width: `100%`,
          border: "solid 1px #EEE",
          padding: 5,
        }}
      >
        {displayConcatenationCheckbox && (
          <div>
            <Tooltip
              title={i18n("SampleFilesConcatenate.checkboxDescription")}
              color={primaryColour}
              placement="right"
            >
              <Checkbox
                style={{ marginRight: SPACE_XS }}
                className="t-concatenation-checkbox"
                onChange={updateSelected}
              />
            </Tooltip>
          </div>
        )}
        <Avatar
          style={{
            backgroundColor: blue6,
            verticalAlign: "middle",
            marginRight: 10,
          }}
          icon={
            files?.length && files.length > 1 ? (
              <SwapOutlined />
            ) : sequenceFile ? (
              <SwapRightOutlined />
            ) : (
              <FileOutlined />
            )
          }
        />
        <List style={{ width: `100%` }} itemLayout="horizontal">
          <List.Item actions={actions}>
            <List.Item.Meta
              title={
                <div
                  style={{ display: "flex", justifyContent: "space-between" }}
                >
                  <span>
                    {files?.length
                      ? files[0].label
                      : sequenceFile
                      ? sequenceFile.label
                      : file.label}
                  </span>
                </div>
              }
              description={formatInternationalizedDateTime(
                files?.length
                  ? files[0].createdDate
                  : sequenceFile
                  ? sequenceFile.createdDate
                  : file.createdDate
              )}
            />
          </List.Item>
          {files?.length && (
            <List.Item actions={actions}>
              <List.Item.Meta
                title={
                  <div
                    style={{ display: "flex", justifyContent: "space-between" }}
                  >
                    <span>{files[1].label}</span>
                  </div>
                }
                description={formatInternationalizedDateTime(
                  files[1].createdDate
                )}
              />
            </List.Item>
          )}
        </List>
      </div>
    </List.Item>
  );
}
