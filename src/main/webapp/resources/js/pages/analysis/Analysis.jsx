/*
 * This file is responsible for displaying the
 * tabs required depending on the analysis state
 * and analysis type.
 */

import { Layout, PageHeader, Skeleton } from "antd";
import React, { Suspense, useContext } from "react";
import {
  IconCheck,
  IconLoading,
  IconWarning,
} from "../../components/icons/Icons";
import { ContentLoading } from "../../components/loader";
import { AnalysisContext } from "../../contexts/AnalysisContext";
import { blue6, green6, grey1, red6 } from "../../styles/colors";

const AnalysisCompletePage = React.lazy(() => import("./AnalysisCompletePage"));
const AnalysisRunningPage = React.lazy(() => import("./AnalysisRunningPage"));
const AnalysisErrorPage = React.lazy(() => import("./AnalysisErrorPage"));

export default function Analysis() {
  const { analysisContext } = useContext(AnalysisContext);
  const { analysisName, loading, isError, isCompleted } = analysisContext;

  let content;
  if (isError) {
    content = <AnalysisErrorPage />;
  } else if (isCompleted) {
    content = <AnalysisCompletePage />;
  } else {
    content = <AnalysisRunningPage />;
  }

  /*
  Depending on the state of the analysis, render a different PageHeader
  avatar
   */
  const avatarOptions = isError
    ? { style: { backgroundColor: red6 }, icon: <IconWarning /> }
    : isCompleted
    ? { style: { backgroundColor: green6 }, icon: <IconCheck /> }
    : { style: { backgroundColor: blue6 }, icon: <IconLoading /> };

  return (
    <Layout style={{ height: `100%`, width: `100%` }}>
      <Skeleton loading={loading} active>
        <PageHeader title={analysisName} avatar={avatarOptions} />
        <Layout.Content
          style={{
            margin: 24,
            marginTop: 0,
            backgroundColor: grey1,
            display: "flex",
          }}
        >
          <Suspense fallback={<ContentLoading />}>{content}</Suspense>
        </Layout.Content>
      </Skeleton>
    </Layout>
  );
}
