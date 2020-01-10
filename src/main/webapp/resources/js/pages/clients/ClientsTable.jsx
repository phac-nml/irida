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
import { ClientGrantsFilter } from "../../components/Tables/filters";
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
      width: 130,
      field: "id",
      filter: "agNumberColumnFilter"
    },
    {
      headerName: i18n("client.clientid"),
      field: "name",
      filter: "agTextColumnFilter",
      cellRenderer: "ClientNameRenderer"
    },
    {
      headerName: i18n("client.grant-types"),
      field: "grants",
      filter: "ClientGrantsFilter",
      cellRenderer: "ClientGrantsRenderer"
    },
    {
      headerName: i18n("iridaThing.timestamp"),
      field: "createdDate",
      filter: "agDateColumnFilter",
      cellRenderer: "DateCellRenderer"
    },
    {
      headerName: i18n("client.details.token.active"),
      field: "tokens",
      filter: "agNumberColumnFilter"
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
                defaultColDef={{ sortable: true }}
                frameworkComponents={{
                  ClientGrantsRenderer,
                  ClientNameRenderer,
                  ClientGrantsFilter,
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
