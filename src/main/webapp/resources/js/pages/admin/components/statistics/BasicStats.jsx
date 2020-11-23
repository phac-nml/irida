/*
 * This file renders the Basic Statistics component
 * with functionality to view advanced statistics
 * charts
 */

import React from "react";
import { Card, Col, Row, Statistic } from "antd";
import { SPACE_MD } from "../../../../styles/spacing";
import { TinyColumn } from "@ant-design/charts";
import { ADMINSTATS } from "../../routes";

import {
  defaultTimePeriod,
  defaultTimePeriodText,
} from "../../statistics-constants";

import { getTinyChartConfiguration } from "../../chart-config";

import styled from "styled-components";
import { blue6 } from "../../../../styles/colors";

import { setBaseUrl } from "../../../../utilities/url-utilities";
import { Link } from "@reach/router";
import { PageWrapper } from "../../../../components/page/PageWrapper";
import { getAdminStatistics } from "../../../../apis/admin/admin";

const LinkCard = styled(Card)`
  &:hover {
    border: ${blue6} solid 1px;
  }
`;

export default function BasicStats() {
  const [cards, setCards] = React.useState([]);

  const DEFAULT_URL = setBaseUrl("/admin/statistics");

  React.useEffect(() => {
    const sum = (a, b) => a + b;
    getAdminStatistics(defaultTimePeriod).then(
      ({
        analysesStats,
        projectStats,
        sampleStats,
        userStats,
        usersLoggedIn,
      }) => {
        setCards([
          {
            key: `analyses`,
            title: i18n("AdminPanelStatistics.basicStatistics.titleAnalysesRan", defaultTimePeriodText),
            value: analysesStats.reduce(sum, 0),
            url: `${DEFAULT_URL}/${ADMINSTATS.ANALYSES}`,
            chartData: analysesStats,
          },
          {
            key: `projects`,
            title: i18n("AdminPanelStatistics.basicStatistics.titleProjectsCreated", defaultTimePeriodText),
            value: projectStats.reduce(sum, 0),
            url: `${DEFAULT_URL}/${ADMINSTATS.PROJECTS}`,
            chartData: projectStats,
          },
          {
            key: `samples`,
            title: i18n("AdminPanelStatistics.basicStatistics.titleSamplesCreated", defaultTimePeriodText),
            value: sampleStats.reduce(sum, 0),
            url: `${DEFAULT_URL}/${ADMINSTATS.SAMPLES}`,
            chartData: sampleStats,
          },
          {
            key: `users`,
            title: i18n("AdminPanelStatistics.basicStatistics.titleUsersCreated", defaultTimePeriodText),
            value: userStats.reduce(sum, 0),
            url: `${DEFAULT_URL}/${ADMINSTATS.USERS}`,
            chartData: userStats,
          },
          {
            key: `usersLoggedIn`,
            title: i18n("AdminPanelStatistics.basicStatistics.titleUsersLoggedOn", defaultTimePeriodText),
            value: usersLoggedIn,
            url: `${DEFAULT_URL}`,
            chartData: [],
          },
        ]);
      }
    );
  }, [DEFAULT_URL]);

  return (
    <PageWrapper title={i18n("AdminPanel.statistics")}>
      <Row
        gutter={[16, 16]}
        className="t-statistics t-stats-basic"
        style={{ padding: SPACE_MD }}
      >
        {cards.map((card) => (
          <Col sm={24} md={12} xl={8} xxl={6} key={card.key}>
            <Link to={card.url}>
              <LinkCard>
                <Statistic title={card.title} value={card.value} />
                <TinyColumn {...getTinyChartConfiguration(card.chartData)} />
              </LinkCard>
            </Link>
          </Col>
        ))}
      </Row>
    </PageWrapper>
  );
}
