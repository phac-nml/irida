import React from "react";
import { render } from "react-dom";
import { Layout, PageHeader, Typography } from "antd";
import { BasicList } from "../../../../components/lists";
import {
  MetadataTemplateProvider,
  useMetadataTemplate,
} from "../../../../contexts/metadata-template-context";
import { IconLoading } from "../../../../components/icons/Icons";
import { formatInternationalizedDateTime } from "../../../../utilities/date-utilities";
import { Template } from "./Template";

const { Content } = Layout;
const { Paragraph } = Typography;

function MetadataTemplatePage() {
  const { template, loading, updateField } = useMetadataTemplate();

  return (
    <PageHeader
      className="site-page-header"
      onBack={() => null}
      title={loading ? "" : template.name}
    >
      <Content>
        <BasicList
          dataSource={[
            {
              title: "TEMPLATE NAME",
              desc: (
                <Paragraph
                  editable={
                    window.project.canManage && !loading
                      ? {
                          onChange: (value) => updateField("name", value),
                        }
                      : null
                  }
                >
                  {loading ? <IconLoading /> : template.name}
                </Paragraph>
              ),
            },
            {
              title: "CREATED DATE",
              desc: loading ? (
                <IconLoading />
              ) : (
                formatInternationalizedDateTime(template.createdDate)
              ),
            },
            {
              title: "TEMPLATE",
              desc: loading ? <IconLoading /> : <Template />,
            },
          ]}
        />
      </Content>
    </PageHeader>
  );
}

render(
  <MetadataTemplateProvider id={1}>
    <MetadataTemplatePage />
  </MetadataTemplateProvider>,
  document.querySelector("#root")
);
