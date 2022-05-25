import React from "react";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { PagedTableProvider, } from "../../../components/ant.design/PagedTable";
import { setBaseUrl } from "../../../utilities/url-utilities";
import SequencingRunListTable from "./SequencingRunListTable";

const URL = setBaseUrl(`ajax/sequencing-runs`);

/**
 * React component to display the sequencing run list page.
 * @returns {*}
 * @constructor
 */
export default function SequencingRunListPage() {

  return (
    <PageWrapper title={i18n("SequencingRunListPage.title")}>
      <PagedTableProvider url={`${URL}/list`}>
        <SequencingRunListTable/>
      </PagedTableProvider>
    </PageWrapper>
  );
}
