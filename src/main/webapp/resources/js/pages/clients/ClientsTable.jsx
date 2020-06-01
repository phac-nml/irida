import React from "react";
import { PagedTable } from "../../components/ant.design/PagedTable";
import { PageWrapper } from "../../components/page/PageWrapper";
import { formatInternationalizedDateTime } from "../../utilities/date-utilities";
import { Button } from "antd";
import { setBaseUrl } from "../../utilities/url-utilities";

export function ClientsTable() {
  const columnDefs = [
    {
      title: i18n("iridaThing.id"),
      key: "id",
      dataIndex: "id"
    },
    {
      title: i18n("client.clientid"),
      key: "name",
      dataIndex: "name",
      render(text, record) {
        return (
          <Button type="link" href={`clients/${record.id}`}>
            {text}
          </Button>
        );
      }
    },
    {
      title: i18n("client.grant-types"),
      key: "grants",
      dataIndex: "grants"
    },
    {
      title: i18n("iridaThing.timestamp"),
      key: "createdDate",
      dataIndex: "createdDate",
      render(text) {
        return formatInternationalizedDateTime(text);
      }
    },
    {
      title: i18n("client.details.token.active"),
      key: "activeTokens",
      dataIndex: "tokens",
      align: "right"
    }
  ];

  return (
    <PageWrapper
      title={i18n("clients.title")}
      headerExtras={
        <Button href={setBaseUrl("clients/create")}>
          {i18n("clients.add")}
        </Button>
      }
    >
      <PagedTable columns={columnDefs} />
    </PageWrapper>
  );
}
