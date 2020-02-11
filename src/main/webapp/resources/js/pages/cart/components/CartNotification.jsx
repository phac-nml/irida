import React from "react";
import PropTypes from "prop-types";
import styled from "styled-components";
import { blue6 } from "../../../styles/colors";
import {
  ExclamationCircleTwoTone,
  ShoppingCartOutlined
} from "@ant-design/icons";

const Wrapper = styled.div`
  font-size: 30px;
  color: ${blue6};
  height: 300px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
`;

const CartNotification = ({ text, type }) => {
  const Icon =
    type === "shopping" ? ShoppingCartOutlined : ExclamationCircleTwoTone;
  return (
    <Wrapper>
      <div>
        <Icon style={{ fontSize: 120 }} />
      </div>
      <div>{text}</div>
    </Wrapper>
  );
};

CartNotification.propTypes = {
  text: PropTypes.string.isRequired,
  icon: PropTypes.string.isRequired
};

export default CartNotification;
