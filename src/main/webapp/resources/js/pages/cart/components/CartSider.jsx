import { Layout } from "antd";
import React from "react";
import styled from "styled-components";
import { IconShoppingCart } from "../../../components/icons/Icons";
import { blue6, grey2 } from "../../../styles/colors";
import CartSamples from "./CartSamples";

const Wrapper = styled.div`
  font-size: 30px;
  color: ${blue6};
  height: 300px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`;

const { Sider } = Layout;

export default function CartSider({ count, collapsed }) {
  return (
    <Sider
      width={400}
      trigger={null}
      collapsible
      collapsed={collapsed}
      collapsedWidth={0}
      style={{ backgroundColor: grey2 }}
    >
      {count === 0 ? (
        <Wrapper>
          <div>
            <IconShoppingCart style={{ fontSize: 120 }} />
          </div>
          <div>{i18n("CartEmpty.heading")}</div>
        </Wrapper>
      ) : (
        <CartSamples />
      )}
    </Sider>
  );
}
