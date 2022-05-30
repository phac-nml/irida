import { Card, List, Skeleton, Space, Typography } from "antd";
import * as React from "react";
import { useParams } from "react-router-dom";
import { BasicList } from "../../../components/lists/index";
import NcbiUploadStates from "../../../components/ncbi/upload-states/index";
import { formatInternationalizedDateTime } from "../../../utilities/date-utilities";
import { setBaseUrl } from "../../../utilities/url-utilities";

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
      console.table(sample);
      return [
        {
          title: i18n("project.export.biosample.title"),
          desc: sample.bioSample,
        },
        {
          title: i18n("project.export.status"),
          desc: <NcbiUploadStates state={sample.submissionStatus} />,
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
      ];
    });

  React.useEffect(() => {
    fetch(`/ajax/ncbi/project/${projectId}/details/${id}`)
      .then((response) => response.json())
      .then((response) => {
        console.log({ response });
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

          <List
            header={
              <Typography.Text strong>
                {i18n("project.export.files")}
              </Typography.Text>
            }
            bordered
            dataSource={samples}
            renderItem={(sample) => (
              <div style={{ paddingTop: 16 }}>
                <BasicList
                  key={sample.bioSample}
                  dataSource={sample}
                  grid={{ gutter: 16, column: 3 }}
                />
              </div>
            )}
          />
        </Space>
      </Card>
    </Skeleton>
  );
}
