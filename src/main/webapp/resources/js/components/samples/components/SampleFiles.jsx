import React from "react";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { Empty, List, Space, Typography } from "antd";
import { IconCalendarTwoTone, IconLoading } from "../../icons/Icons";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { SPACE_XS } from "../../../styles/spacing";
import { CalendarDate } from "../../CalendarDate";

const { Text } = Typography;

export function SampleFiles({ id, projectId }) {
  const [loading, setLoading] = React.useState(true);
  const [files, setFiles] = React.useState();

  React.useEffect(() => {
    fetch(
      setBaseUrl(
        `/ajax/samples/${id}/files${projectId ? `?projectId=${projectId}` : ``}`
      )
    )
      .then((response) => response.json())
      .then((data) => {
        Object.keys(data).forEach(
          (key) => !data[key].length && delete data[key]
        );
        setFiles(data);
        setLoading(false);
      });
  }, [id, projectId]);

  const labels = {
    singles: i18n("SampleFiles.singles"),
    paired: i18n("SampleFiles.paired"),
    fast5: i18n("SampleFiles.fast5"),
    assemblies: i18n("SampleFiles.assemblies"),
  };

  return loading ? (
    <IconLoading />
  ) : Object.keys(files).length !== 0 ? (
    <>
      <List
        itemLayout="vertical"
        dataSource={Object.keys(files)}
        renderItem={(type) => (
          <List.Item>
            <List.Item.Meta
              key={type}
              title={<Text strong>{labels[type]}</Text>}
              description={
                <List
                  dataSource={files[type]}
                  renderItem={(file) => (
                    <List.Item key={`single-${file.identifier}`}>
                      <List.Item.Meta
                        description={
                          <Space direction="vertical">
                            <Text>{file.label}</Text>
                            <CalendarDate date={file.createdDate} />
                          </Space>
                        }
                      />
                    </List.Item>
                  )}
                />
              }
            />
          </List.Item>
        )}
      />
    </>
  ) : (
    <Empty description={i18n("SampleFiles.no-files")} />
  );
}
