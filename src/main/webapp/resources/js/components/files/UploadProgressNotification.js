import { Alert, notification, Progress, Tooltip } from "antd";
import { SPACE_XS } from "../../styles/spacing";
import { IconCloseCircle } from "../icons/Icons";
import { blue6 } from "../../styles/colors";
import React from "react";

export function UploadProgressNotification({ request, names }) {
  this.key = Date.now();
  this.names = names;
  this.request = request;
}

UploadProgressNotification.prototype.cancelUpload = function() {
  this.request.cancel();
  this.showUploadPercentageNotification({
    progress: 0,
    duration: 4,
    cancelled: true
  });
};

UploadProgressNotification.prototype.show = function(progress) {
  this.showUploadPercentageNotification({
    progress
  });
};

UploadProgressNotification.prototype.showUploadPercentageNotification = function({
  progress,
  duration = 0,
  cancelled = false
}) {
  const cancelFn = this.cancelUpload.bind(this);
  notification.info({
    key: this.key,
    style: { width: 400 },
    closeIcon: <span />,
    message: i18n("FileUploader.progress.title"),
    description: (
      <>
        {i18n("FileUploader.progress.desc")}
        {
          <ul className="t-file-upload">
            {this.names.map(name => (
              <li key={name}>{name}</li>
            ))}
          </ul>
        }
        {cancelled ? (
          <Alert
            message={i18n("FileUploader.progress.cancelled")}
            type="info"
          />
        ) : (
          <>
            <div
              style={{
                display: "flex",
                alignItems: "center",
                marginBottom: SPACE_XS
              }}
            >
              <Progress
                style={{ flexGrow: 1 }}
                percent={progress}
                size="small"
              />
              <Tooltip
                title={i18n("FileUploader.progress.tooltip")}
                placement="topRight"
              >
                <button
                  style={{ border: "none", backgroundColor: "transparent" }}
                  onClick={cancelFn}
                >
                  <IconCloseCircle style={{ color: blue6 }} />
                </button>
              </Tooltip>
            </div>
            <Alert
              message={i18n("FileUploader.progress.warning")}
              type="warning"
            />
          </>
        )}
      </>
    ),
    placement: "bottomRight",
    duration
  });
};
