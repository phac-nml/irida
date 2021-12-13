import React from "react";
import { Avatar, Button, List, notification } from "antd";
import { SPACE_XS } from "../../styles/spacing";
import { IconDownloadFile, IconRemove } from "../icons/Icons";
import { deleteSampleFiles } from "../../apis/samples/samples";
import { removeSampleFiles } from "../samples/sampleFilesSlice";
import { useDispatch, useSelector } from "react-redux";

export function SequenceFileDetailsRenderer({
  file,
  isForwardFile,
  sampleId,
  sequencingObjectId,
}) {
  const dispatch = useDispatch();

  const removeSequencingObjectFromSample = () => {
    deleteSampleFiles({ sampleId, sequencingObjectId })
      .then(() => {
        notification.success({
          message: "SUCCESSFULLY REMOVED SEQUENCING OBJECT FROM SAMPLE",
        });
        dispatch(removeSampleFiles({ sequencingObjectId }));
      })
      .catch((error) => {
        notification.error({ message: error });
      });
  };

  return (
    <List.Item key={`file-${file.id}`} style={{ width: `100%` }}>
      <List.Item.Meta
        avatar={<Avatar size={`small`} icon={file.icon} />}
        title={
          <div style={{ display: "flex", justifyContent: "space-between" }}>
            <a href={file.fastqcLink} target="_blank">
              {file.label}
            </a>
            <span>
              <span style={{ marginRight: SPACE_XS }}>{file.filesize}</span>
              <Button
                style={{ marginRight: SPACE_XS }}
                shape="circle"
                icon={<IconDownloadFile />}
              />
              {isForwardFile && (
                <Button
                  shape="circle"
                  icon={<IconRemove />}
                  onClick={() => removeSequencingObjectFromSample()}
                />
              )}
            </span>
          </div>
        }
      />
    </List.Item>
  );
}
