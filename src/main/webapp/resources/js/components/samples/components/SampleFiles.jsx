import React from "react";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { List, Typography } from "antd";
import { IconCalendarTwoTone } from "../../icons/Icons";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { SPACE_XS } from "../../../styles/spacing";

const { Text } = Typography;

export function SampleFiles({ id, projectId }) {
  const [loading, setLoading] = React.useState(true);
  const [files, setFiles] = React.useState();

  console.log({ projectId });

  React.useEffect(() => {
    fetch(
      setBaseUrl(
        `/ajax/samples/${id}/files${projectId ? `?projectId=${projectId}` : ``}`
      )
    )
      .then((response) => response.json())
      .then((data) => {
        setFiles(data);
        setLoading(false);
      });
  }, [id]);

  return loading ? (
    "LOADING..."
  ) : (
    <>
      <List itemLayout="vertical">
        <List.Item>
          <List.Item.Meta
            title={"SINGLES"}
            description={
              <List
                dataSource={files.singles}
                renderItem={(file) => (
                  <List.Item key={`single-${file.identifier}`}>
                    <List.Item.Meta
                      description={
                        <div>
                          <div>
                            <Text strong>{file.label}</Text>
                          </div>
                          <IconCalendarTwoTone
                            style={{ marginRight: SPACE_XS }}
                          />
                          {formatInternationalizedDateTime(file.createdDate)}
                        </div>
                      }
                    />
                  </List.Item>
                )}
              />
            }
          />
        </List.Item>
        <List.Item>
          <List.Item.Meta
            title={"PAIRED"}
            description={
              <List
                dataSource={files.paired}
                renderItem={(file) => (
                  <List.Item key={`single-${file.identifier}`}>
                    <List.Item.Meta description={file.label} />
                  </List.Item>
                )}
              />
            }
          />
        </List.Item>
      </List>
    </>
  );
}
