/*
 * This file renders the Basic Statistics component
 * with functionality to view advanced statistics
 * charts
 */

import React, { useContext } from "react";
import { Card, Col, Row, Statistic } from "antd";
import { SPACE_MD } from "../../../../styles/spacing";
import { TinyColumn } from "@ant-design/charts";
import { ADMINSTATS } from "../../routes";

import {
  AdminStatisticsContext,
  chartTypes,
  defaultTimePeriod,
  timePeriodMap,
} from "../../../../contexts/AdminStatisticsContext";

import { getChartConfiguration } from "../../chart-config";
import styled from "styled-components";
import { blue6 } from "../../../../styles/colors";

import { setBaseUrl } from "../../../../utilities/url-utilities";
import { Link } from "@reach/router";

const LinkCard = styled(Card)`
  &:hover {
    border: ${blue6} solid 1px;
  }
`;

export default function BasicStats() {
  const { adminStatisticsContext } = useContext(AdminStatisticsContext);

  const DEFAULT_URL = setBaseUrl("/admin/statistics");

  const cards = [
    {
      key: `analyses`,
      title: `Analyses run in past ${timePeriodMap[defaultTimePeriod]}`,
      value: adminStatisticsContext.basicStats.analysesRun,
      url: `${DEFAULT_URL}/${ADMINSTATS.ANALYSES}`,
    },
    {
      key: `projects`,
      title: `Projects created in past ${timePeriodMap[defaultTimePeriod]}`,
      value: adminStatisticsContext.basicStats.projectsCreated,
      url: `${DEFAULT_URL}/${ADMINSTATS.PROJECTS}`,
    },
    {
      key: `samples`,
      title: `Samples created in past ${timePeriodMap[defaultTimePeriod]}`,
      value: adminStatisticsContext.basicStats.samplesCreated,
      url: `${DEFAULT_URL}/${ADMINSTATS.SAMPLES}`,
    },
    {
      key: `users`,
      title: `Users created in past ${timePeriodMap[defaultTimePeriod]}`,
      value: adminStatisticsContext.basicStats.usersCreated,
      url: `${DEFAULT_URL}/${ADMINSTATS.USERS}`,
    },
    {
      key: `usersLoggedIn`,
      title: `Users logged on in past ${timePeriodMap[defaultTimePeriod]}`,
      value: adminStatisticsContext.basicStats.usersLoggedIn,
      url: `${DEFAULT_URL}`,
    },
  ];
  return (
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
              <TinyColumn
                {...getChartConfiguration(
                  chartTypes.TINYCOLUMN,
                  card.key,
                  adminStatisticsContext.statistics
                )}
              />
            </LinkCard>
          </Link>
        </Col>
      ))}
    </Row>
  );
}
