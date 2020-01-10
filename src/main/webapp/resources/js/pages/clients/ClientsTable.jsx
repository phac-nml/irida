import React, { useEffect, useState } from "react";
import { PageWrapper } from "../../components/page/PageWrapper";
import { Button } from "antd";
import { setBaseUrl } from "../../utilities/url-utilities";
import { AgGridReact } from "ag-grid-react";
import "ag-grid-community/dist/styles/ag-grid.css";
import "ag-grid-community/dist/styles/ag-theme-material.css";
import { getAllClients } from "../../apis/clients/clients";
import { AgGridLayout } from "../../components/Tables/AgGridLayout";
import {
  ClientGrantsRenderer,
  ClientNameRenderer,
  DateCellRenderer
} from "../../components/Tables/renderers";
import { AutoSizer } from "react-virtualized";

export function ClientsTable() {
  const [rows, setRows] = useState([]);

  useEffect(() => {
    getAllClients().then(data => {
      setRows(data.models);
    });
  }, [getAllClients]);

  const columnDefs = [
    {
      headerName: i18n("iridaThing.id"),
      width: 100,
      field: "id"
    },
    {
      headerName: i18n("client.clientid"),
      field: "name",
      cellRenderer: "ClientNameRenderer"
    },
    {
      headerName: i18n("client.grant-types"),
      field: "grants",
      cellRenderer: "ClientGrantsRenderer"
    },
    {
      headerName: i18n("iridaThing.timestamp"),
      field: "createdDate",
      cellRenderer: "DateCellRenderer"
    },
    {
      headerName: i18n("client.details.token.active"),
      field: "tokens"
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
      <AutoSizer>
        {({ height, width }) => {
          return (
            <AgGridLayout height={height} width={width}>
              <AgGridReact
                columnDefs={columnDefs}
                rowData={rows}
                frameworkComponents={{
                  ClientGrantsRenderer,
                  ClientNameRenderer,
                  DateCellRenderer
                }}
              />
            </AgGridLayout>
          );
        }}
      </AutoSizer>
    </PageWrapper>
  );
}
