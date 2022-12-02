import React from "react";
import { useGetCartCountQuery } from "../../../redux/endpoints/cart";
import { ShoppingCartOutlined } from "@ant-design/icons";
import { Badge } from "antd";
import { ROUTE_CART } from "../../../data/routes";

/**
 * React component to render the cart icon in the main navigation and display
 * the number of samples in the cart.
 * @constructor
 */
export default function CartLink() {
  const { data: count } = useGetCartCountQuery(undefined, {});

  return (
    <Badge count={count} offset={[-5, 0]}>
      <a className={"nav-icon"} href={ROUTE_CART}>
        <ShoppingCartOutlined />
      </a>
    </Badge>
  );
}
