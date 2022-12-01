import React from "react";
import { useGetCartCountQuery } from "../../../redux/services/cart";
import { ShoppingCartOutlined } from "@ant-design/icons";
import { Badge, Button } from "antd";
import { ROUTE_CART } from "../../../data/routes";

export default function CartLink() {
  const { data: count } = useGetCartCountQuery(undefined, {});

  return (
    <Badge count={count} showZero>
      <Button type={"link"} href={ROUTE_CART}>
        <ShoppingCartOutlined />
      </Button>
    </Badge>
  );
}
