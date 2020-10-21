/*
 * This file is responsible for displaying the
 * tabs required
 */

import React, { Suspense } from "react";
import { setBaseUrl } from "../../../utilities/url-utilities";
import { PageWrapper } from "../../../components/page/PageWrapper";
import { Link, Location, Router } from "@reach/router";
import { Menu } from "antd";
import { ContentLoading } from "../../../components/loader";

import { SPACE_MD } from "../../../styles/spacing"

const FastQCDetails = React.lazy(() => import("./FastQCDetails"));
const FastQCImages = React.lazy(() => import("./FastQCImages"));
const OverRepresentedSequences = React.lazy(() => import("./OverRepresentedSequences"));

export default function FastQC() {

  const regExp1 = /projects\/(\d+)\/samples\/(\d+)\/sequenceFiles\/(\d+)\/file\/(\d+)\/summary?/i;
  const regExp2 = /projects\/(\d+)\/samples\/(\d+)\/sequenceFiles\/(\d+)\/file\/(\d+)?/i;
  const regExp3 = /sequenceFiles\/(\d+)\/file\/(\d+)\/summary?/i;
  const regExp4 = /sequencingRuns\/(\d+)\/sequenceFiles\/(\d+)\/file\/(\d+)\/summary?/i;
  const found = window.location.href.match(regExp1) || window.location.href.match(regExp2) || window.location.href.match(regExp3) || window.location.href.match(regExp4);

  // const [selectedKeys, setSelectedKeys] = React.useState(() => {
  //   // Grab it from the URL
  //   return found !== null ? found[1] : "images";
  // });

  const DEFAULT_URL = setBaseUrl(found[0]);

  return (
    <PageWrapper title="08-5578-small_S1_L001_R1_001.fastq">
      <Location>
        {() => {
          return (
            <Menu
              mode="horizontal"
            >
              <Menu.Item key="images">
                <Link to={`${DEFAULT_URL}/images`} >
                  FastQC Images
                </Link>
              </Menu.Item>
              <Menu.Item key="overrepresented">
                <Link to={`${DEFAULT_URL}/overrepresented`} >
                  Overrepresented Sequences
                </Link>
              </Menu.Item>
              <Menu.Item key="details">
                <Link to={`${DEFAULT_URL}/details`} >
                  Details
                </Link>
              </Menu.Item>
            </Menu>
          );
        }}
      </Location>
      <Suspense fallback={<ContentLoading />}>
          <Router style={{ paddingTop: SPACE_MD }}>
            <FastQCImages path={`${DEFAULT_URL}/images`} default key="images"/>
            <OverRepresentedSequences path={`${DEFAULT_URL}/overrepresented`} key="overrepresented"/>
            <FastQCDetails path={`${DEFAULT_URL}/details`} key="details"/>
          </Router>
      </Suspense>
    </PageWrapper>
  );
}
