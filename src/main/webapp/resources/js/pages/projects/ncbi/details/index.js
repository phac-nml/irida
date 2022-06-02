import { Avatar, Card, Dropdown, List, Menu, Skeleton, Space } from "antd";
import * as React from "react";
import { useParams } from "react-router-dom";
import { BasicList } from "../../../../components/lists";
import NcbiUploadStates from "../../../../components/ncbi/upload-states";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import { setBaseUrl } from "../../../../utilities/url-utilities";
import {
  DownOutlined,
  SwapOutlined,
  SwapRightOutlined,
} from "@ant-design/icons";
import { blue6 } from "../../../../styles/colors";

function SequenceObjectListItem({ sequenceObject, actions = [] }) {
  const { label, createdDate, files } = sequenceObject;
  // TODO (Josh - 6/2/22): Clean this up and add single sequence file use
  return (
    <List.Item>
      <div
        style={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          width: `100%`,
        }}
      >
        <Avatar
          style={{ backgroundColor: blue6, verticalAlign: "middle" }}
          icon={files.length > 1 ? <SwapOutlined /> : <SwapRightOutlined />}
        />
        <List style={{ width: `100%` }} itemLayout="horizontal">
          <List.Item actions={actions}>
            <List.Item.Meta
              title={files[0].label}
              description={formatInternationalizedDateTime(
                files[0].createdDate
              )}
            />
          </List.Item>
          <List.Item actions={actions}>
            <List.Item.Meta
              title={files[1].label}
              description={formatInternationalizedDateTime(
                files[1].createdDate
              )}
            />
          </List.Item>
        </List>
      </div>
    </List.Item>
  );
}

export default function NCBIExportDetails() {
  const { projectId, id } = useParams();

  const [details, setDetails] = React.useState([]);
  const [samples, setSamples] = React.useState([]);
  const [loading, setLoading] = React.useState(true);

  const formatResponse = (data) => [
    {
      title: i18n("iridaThing.id"),
      desc: data.id,
    },
    {
      title: i18n("project.export.status"),
      desc: <NcbiUploadStates state={data.state} />,
    },
    {
      title: i18n("project.export.submitter"),
      desc: (
        <a href={setBaseUrl(`/users/${data.submitter.id}`)}>
          {data.submitter.label}
        </a>
      ),
    },
    {
      title: i18n("iridaThing.timestamp"),
      desc: formatInternationalizedDateTime(data.createdDate),
    },
    {
      title: i18n("project.export.bioproject.title"),
      desc: data.bioProject,
    },
    {
      title: i18n("project.export.organization.title"),
      desc: data.organization,
    },
    {
      title: i18n("project.export.namespace.title"),
      desc: data.ncbiNamespace,
    },
    {
      title: i18n("project.export.release_date.title"),
      desc: data.releaseDate
        ? formatInternationalizedDateTime(data.releaseDate)
        : "Not Released",
    },
  ];

  const formatSamples = (samples) =>
    samples.map((sample) => {
      return {
        details: [
          {
            title: i18n("project.export.biosample.title"),
            desc: sample.bioSample,
          },
          {
            title: i18n("project.export.status"),
            desc: sample.submissionStatus, //<NcbiUploadStates state={sample.submissionStatus} />,
          },
          {
            title: i18n("project.export.accession"),
            desc: sample.accession,
          },
          {
            title: i18n("project.export.library_name.title"),
            desc: sample.libraryName,
          },
          {
            title: i18n("project.export.instrument_model.title"),
            desc: sample.instrumentModel,
          },
          {
            title: i18n("project.export.library_strategy.title"),
            desc: sample.libraryStrategy,
          },
          {
            title: i18n("project.export.library_source.title"),
            desc: sample.librarySource,
          },
          {
            title: i18n("project.export.library_construction_protocol.title"),
            desc: sample.libraryConstructionProtocol,
          },
        ],
        files: {
          singles: sample.singles,
          pairs: sample.pairs,
        },
      };
    });

  React.useEffect(() => {
    fetch(`/ajax/ncbi/project/${projectId}/details/${id}`)
      .then((response) => response.json())
      .then((response) => {
        setDetails(formatResponse(response));
        setSamples(formatSamples(response.samples));
        setLoading(false);
      });
  }, [id, projectId]);

  return (
    <Skeleton active={true} loading={loading}>
      <Card title={i18n("project.export.sidebar.title")}>
        <Space direction="vertical" style={{ width: `100%` }}>
          <BasicList dataSource={details} grid={{ gutter: 16, column: 2 }} />

          {samples.map((sample, index) => {
            return (
              <Card key={`sample-${index}`}>
                <BasicList
                  dataSource={sample.details}
                  grid={{ gutter: 16, column: 3 }}
                />
                <List bordered title={"FILES"}>
                  {sample.files.pairs.map((pair) => {
                    return (
                      <SequenceObjectListItem
                        key={pair.label}
                        sequenceObject={pair}
                        actions={[
                          <Dropdown
                            overlay={
                              <Menu>
                                <Menu.Item>Forward</Menu.Item>
                                <Menu.Item>Reverse</Menu.Item>
                              </Menu>
                            }
                          >
                            <a onClick={(e) => e.preventDefault()}>
                              <Space key={`download-${pair.label}`}>
                                download
                                <DownOutlined />
                              </Space>
                            </a>
                          </Dropdown>,
                        ]}
                      />
                    );
                  })}
                  {sample.files.singles.map((single) => {
                    return (
                      <SequenceObjectListItem
                        key={single.label}
                        sequenceObject={single}
                      />
                    );
                  })}
                </List>
              </Card>
            );
          })}
        </Space>
      </Card>
    </Skeleton>
  );
}
