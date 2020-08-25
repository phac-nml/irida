import React, { useContext, useState } from "react";
import {
  PagedTable,
  PagedTableContext,
} from "../../../../../components/ant.design/PagedTable";
import {
  Button,
  Form,
  Input,
  List,
  PageHeader,
  Popconfirm,
  Radio,
  Tag,
  Typography,
} from "antd";
import { setBaseUrl } from "../../../../../utilities/url-utilities";
import { dateColumnFormat } from "../../../../../components/ant.design/table-renderers";
import { revokeClientTokens } from "../../../../../apis/clients/clients";
import { IconStop } from "../../../../../components/icons/Icons";
import { REFRESH_TOKEN_VALIDITY, TOKEN_VALIDITY } from "../constants";
import { SPACE_MD } from "../../../../../styles/spacing";

const { Paragraph } = Typography;
const { Item } = Form;

/**
 * Table for displaying a list of clients.
 * @return {*}
 * @constructor
 */
export function ClientsTable() {
  const { updateTable } = useContext(PagedTableContext);

  const columns = [
    {
      title: i18n("ClientsTable.column.id"),
      width: 80,
      dataIndex: "id",
      sorter: true,
    },
    {
      title: i18n("ClientsTable.column.clientId"),
      dataIndex: "name",
      ellipsis: true,
      sorter: true,
      render(text, item) {
        return (
          <a className="t-client-name" href={setBaseUrl(`/clients/${item.id}`)}>
            {text}
          </a>
        );
      },
    },
    {
      title: i18n("ClientsTable.column.grants"),
      dataIndex: "details",
      render(grants, item) {
        const colors = {
          password: "purple",
          authorization_code: "volcano",
          refresh_token: "magenta",
        };
        return (
          <div>
            {item.details.authorizedGrantTypes.map((g) => (
              <Tag color={colors[g] || ""} key={g}>
                {g}
              </Tag>
            ))}
          </div>
        );
      },
    },
    {
      ...dateColumnFormat(),
      title: i18n("ClientsTable.column.created"),
      dataIndex: "createdDate",
    },
    {
      title: i18n("ClientsTable.column.activeTokens"),
      dataIndex: "tokens",
      align: "right",
    },
    {
      key: "action",
      align: "right",
      fixed: "right",
      width: 200,
      render(text, record) {
        const disabled = !record.tokens;
        return (
          <Popconfirm
            disabled={disabled}
            title={i18n("client.revoke.confirm", record.name)}
            placement={"topRight"}
            onConfirm={() => revokeTokens(record.id)}
          >
            <Button disabled={disabled}>
              <IconStop />
              {i18n("client.details.token.revoke")}
            </Button>
          </Popconfirm>
        );
      },
    },
  ];

  /**
   * Revoke the tokens for the current client described
   * in the current row.
   */
  function revokeTokens(id) {
    revokeClientTokens(id).then(updateTable);
  }

  const radioStyle = { display: "block", lineHeight: `35px` };
  const [grantType, setGrantType] = useState("password");

  return (
    <PagedTable
      className={"t-admin-clients-table"}
      columns={columns}
      expandable={{
        expandedRowRender: (record) => (
          <div>
            <PageHeader
              title={record.name}
              extra={[
                <Button key={`revoke-${record.id}`}>REVOKE</Button>,
                <Button key={`remove-${record.id}`}>REMOVED</Button>,
              ]}
            />
            <List>
              <List.Item>
                <List.Item.Meta
                  title={"CLIENT SECRET"}
                  description={
                    <Paragraph copyable>
                      {record.details.clientSecret}
                    </Paragraph>
                  }
                />
              </List.Item>
            </List>
            <Form
              layout="vertical"
              initialValues={{
                tokenValidity: record.details.accessTokenValiditySeconds,
                grantType: record.details.authorizedGrantTypes[0],
                refreshToken:
                  record.details.refreshTokenValiditySeconds === null
                    ? 0
                    : record.details.refreshTokenValiditySeconds,
                read: "read",
                write: "no",
              }}
            >
              <Item
                label={i18n("AddClientForm.tokenValidity")}
                name="tokenValidity"
              >
                <Radio.Group>
                  {TOKEN_VALIDITY.map((token) => (
                    <Radio.Button key={token.value} value={token.value}>
                      {token.text}
                    </Radio.Button>
                  ))}
                </Radio.Group>
              </Item>
              <Item label={i18n("AddClientForm.grantTypes")} name="grantType">
                <Radio.Group onChange={(e) => setGrantType(e.target.value)}>
                  <Radio style={radioStyle} value="password">
                    {i18n("AddClientForm.grant.password")}
                  </Radio>
                  <Radio style={radioStyle} value="authorization_code">
                    {i18n("AddClientForm.grant.authorizationCode")}

                    {grantType === "authorization_code" ? (
                      <Item
                        name="redirectURI"
                        style={{
                          display: "inline-block",
                          marginLeft: SPACE_MD,
                          marginBottom: 0,
                          width: 400,
                        }}
                        rules={[
                          {
                            required: true,
                            message: i18n(
                              "AddClientForm.grant.authorizationCode.redirect.warning"
                            ),
                          },
                        ]}
                      >
                        <Input
                          placeholder={i18n(
                            "AddClientForm.grant.authorizationCode.redirect"
                          )}
                        />
                      </Item>
                    ) : null}
                  </Radio>
                </Radio.Group>
              </Item>
              <Item
                label={i18n("AddClientForm.refreshToken")}
                name="refreshToken"
              >
                <Radio.Group>
                  {REFRESH_TOKEN_VALIDITY.map((token) => (
                    <Radio.Button key={token.value} value={token.value}>
                      {token.text}
                    </Radio.Button>
                  ))}
                </Radio.Group>
              </Item>
              <Item label={i18n("AddClientForm.readScope")} name="read">
                <Radio.Group>
                  <Radio.Button value="no">
                    {i18n("AddClientForm.scopeNotAllowed")}
                  </Radio.Button>
                  <Radio.Button value="read">
                    {i18n("AddClientForm.scopeAllowed")}
                  </Radio.Button>
                  <Radio.Button value="auto">
                    {i18n("AddClientForm.scopeAllowedAutoApprove")}
                  </Radio.Button>
                </Radio.Group>
              </Item>
              <Item label={i18n("AddClientForm.writeScope")} name="write">
                <Radio.Group>
                  <Radio.Button value="no">
                    {i18n("AddClientForm.scopeNotAllowed")}
                  </Radio.Button>
                  <Radio.Button value="write">
                    {i18n("AddClientForm.scopeAllowed")}
                  </Radio.Button>
                  <Radio.Button value="auto">
                    {i18n("AddClientForm.scopeAllowedAutoApprove")}
                  </Radio.Button>
                </Radio.Group>
              </Item>
            </Form>
          </div>
        ),
      }}
    />
  );
}
